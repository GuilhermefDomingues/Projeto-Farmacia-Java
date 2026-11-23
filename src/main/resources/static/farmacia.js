const selecionar = (id) => document.getElementById(id);
const lista = selecionar("lista");
const busca = selecionar("busca");
const aviso = selecionar("aviso");
const editor = selecionar("editor");
const formulario = selecionar("formulario");
const campos = selecionar("campos");
const erro = selecionar("erro");
const salvar = selecionar("salvar");
const moeda = new Intl.NumberFormat("pt-BR", { style: "currency", currency: "BRL" });

const paginas = {
    medicamentos: {
        titulo: "Medicamentos", novo: "Novo medicamento", singular: "medicamento",
        busca: "Buscar por medicamento ou fabricante",
        campos: [
            ["nome", "Nome", "text", { maxlength: 255 }],
            ["fabricante", "Fabricante", "text", { maxlength: 255 }],
            ["preco", "Preço (R$)", "number", { min: "0.01", step: "0.01", max: "99999999.99" }],
            ["quantidadeEstoque", "Quantidade em estoque", "number", { min: 0, max: 2147483647 }],
            ["dataValidade", "Data de validade", "date"]
        ]
    },
    clientes: {
        titulo: "Clientes", novo: "Novo cliente", singular: "cliente",
        busca: "Buscar por nome, e-mail ou telefone",
        campos: [
            ["nome", "Nome", "text", { maxlength: 255 }],
            ["email", "E-mail", "email", { maxlength: 255 }],
            ["telefone", "Telefone (opcional)", "tel", { maxlength: 30, opcional: true }]
        ]
    },
    vendas: {
        titulo: "Vendas", novo: "Registrar venda", singular: "venda",
        busca: "Buscar por número, cliente ou medicamento"
    }
};

let pagina = "medicamentos";
let dados = [];
let clientes = [];
let medicamentos = [];
let edicao = null;
let carregamento = 0;
let carregando = false;
let falhou = false;
let enviando = false;

async function api(caminho, metodo = "GET", corpo) {
    const resposta = await fetch(caminho, {
        method: metodo,
        headers: corpo ? { "Content-Type": "application/json" } : undefined,
        body: corpo ? JSON.stringify(corpo) : undefined
    });
    if (!resposta.ok) {
        const problema = await resposta.json().catch(() => ({}));
        const mensagens = {
            400: "Confira os campos enviados.",
            404: "Registro não encontrado. Atualize a lista.",
            409: "A operação entra em conflito com os dados atuais."
        };
        throw new Error(problema.detail || problema.message || mensagens[resposta.status] || "Erro na API (" + resposta.status + ").");
    }
    if (resposta.status !== 204) return resposta.json();
}

function informar(mensagem, tipo = "success") {
    aviso.textContent = mensagem;
    aviso.className = "notice " + tipo;
    aviso.hidden = !mensagem;
}

function elemento(tag, texto, classe) {
    const no = document.createElement(tag);
    if (texto != null) no.textContent = texto;
    if (classe) no.className = classe;
    return no;
}

function botao(texto, classe, acao) {
    const no = elemento("button", texto, classe);
    no.type = "button";
    no.addEventListener("click", acao);
    return no;
}

function dataBr(data) {
    return data.slice(0, 10).split("-").reverse().join("/");
}

function clienteNome(id) {
    return clientes.find((cliente) => cliente.id === id)?.nome ?? ("Cliente #" + id);
}

function medicamentoNome(id) {
    return medicamentos.find((medicamento) => medicamento.id === id)?.nome ?? ("Medicamento #" + id);
}

function criarCartao(item) {
    const cartao = elemento("article", null, "card");
    cartao.append(elemento("span", "#" + item.id, "badge"));
    if (pagina === "vendas") {
        cartao.append(
            elemento("h3", "Venda #" + item.id),
            elemento("p", clienteNome(item.clienteId)),
            elemento("p", item.quantidade + " un. de " + medicamentoNome(item.medicamentoId)),
            elemento("p", dataBr(item.dataVenda)),
            elemento("p", moeda.format(item.total), "price")
        );
        return cartao;
    }

    cartao.append(elemento("h3", item.nome));
    if (pagina === "medicamentos") {
        cartao.append(
            elemento("p", item.fabricante),
            elemento("p", moeda.format(item.preco), "price"),
            elemento("p", item.quantidadeEstoque + " em estoque • Val. " + dataBr(item.dataValidade))
        );
    } else {
        cartao.append(elemento("p", item.email), elemento("p", item.telefone || "Telefone não informado"));
    }

    const acoes = elemento("div", null, "card-actions");
    acoes.append(botao("Editar", "secondary", () => abrirEditor(item)));
    if (pagina === "medicamentos") {
        acoes.append(botao("Excluir", "danger", (evento) => excluir(item, evento.currentTarget)));
    }
    cartao.append(acoes);
    return cartao;
}

function normalizar(texto) {
    return String(texto).normalize("NFD").replace(/[\u0300-\u036f]/g, "").toLowerCase();
}

function renderizar() {
    lista.replaceChildren();
    selecionar("contador").textContent = "";
    lista.setAttribute("aria-busy", String(carregando));
    if (carregando || falhou) {
        const bloco = elemento("div", carregando ? "Carregando…" : "Não foi possível carregar os registros.", "empty");
        if (falhou) {
            bloco.append(" ", botao("Tentar de novo", "secondary", carregar));
        }
        lista.append(bloco);
        return;
    }

    const filtro = normalizar(busca.value.trim());
    const encontrados = dados.filter((item) => {
        const termos = pagina === "vendas"
            ? [item.id, clienteNome(item.clienteId), medicamentoNome(item.medicamentoId)]
            : [item.id, item.nome, item.fabricante, item.email, item.telefone];
        return termos.some((termo) => termo != null && normalizar(termo).includes(filtro));
    });
    selecionar("contador").textContent = encontrados.length + (encontrados.length === 1 ? " registro" : " registros");
    if (!encontrados.length) {
        lista.append(elemento("p", filtro ? "Nenhum resultado para essa busca." : "Ainda não há registros nesta seção.", "empty"));
        return;
    }
    const grade = elemento("div", null, "grid");
    encontrados.forEach((item) => grade.append(criarCartao(item)));
    lista.append(grade);
}

async function carregar() {
    const atual = ++carregamento;
    const secao = pagina;
    carregando = true;
    falhou = false;
    renderizar();
    try {
        if (secao === "vendas") {
            const resultados = await Promise.all([api("/vendas"), api("/clientes"), api("/medicamentos")]);
            if (atual !== carregamento) return;
            [dados, clientes, medicamentos] = resultados;
        } else {
            const registros = await api("/" + secao);
            if (atual !== carregamento) return;
            dados = registros;
        }
    } catch (problema) {
        if (atual !== carregamento) return;
        falhou = true;
        informar(problema.message, "failure");
    } finally {
        if (atual === carregamento) {
            carregando = false;
            renderizar();
        }
    }
}

function mudarPagina(secao) {
    pagina = secao;
    document.querySelectorAll("[data-page]").forEach((botao) => {
        if (botao.dataset.page === secao) botao.setAttribute("aria-current", "page");
        else botao.removeAttribute("aria-current");
    });
    selecionar("secao").textContent = paginas[pagina].titulo;
    selecionar("novo").textContent = paginas[pagina].novo;
    busca.placeholder = paginas[pagina].busca;
    busca.value = "";
    informar("");
    carregar();
}

function adicionarCampo(nome, rotulo, tipo, opcoes = {}, valor) {
    const label = elemento("label", rotulo);
    const input = elemento("input");
    input.name = nome;
    input.type = tipo;
    input.required = !opcoes.opcional;
    Object.entries(opcoes).forEach(([chave, valor]) => {
        if (chave !== "opcional") input.setAttribute(chave, valor);
    });
    input.value = valor ?? "";
    label.append(input);
    campos.append(label);
}

function adicionarSelecao(nome, rotulo, registros, texto) {
    const label = elemento("label", rotulo);
    const select = elemento("select");
    select.name = nome;
    select.required = true;
    const escolha = elemento("option", "Selecione");
    escolha.value = "";
    select.append(escolha);
    registros.forEach((registro) => {
        const opcao = elemento("option", texto(registro));
        opcao.value = registro.id;
        select.append(opcao);
    });
    label.append(select);
    campos.append(label);
}

function abrirEditor(item = null) {
    if (pagina === "vendas" && (carregando || falhou)) {
        informar("Aguarde o carregamento de clientes e medicamentos.", "failure");
        return;
    }
    edicao = item;
    erro.hidden = true;
    campos.replaceChildren();
    const config = paginas[pagina];
    selecionar("editor-titulo").textContent = item ? "Editar " + config.singular : config.novo;
    salvar.textContent = pagina === "vendas" ? "Registrar venda" : "Salvar";

    if (pagina === "vendas") {
        const disponiveis = medicamentos.filter((item) => item.quantidadeEstoque > 0);
        if (!clientes.length || !disponiveis.length) {
            informar("Cadastre um cliente e um medicamento com estoque antes de registrar a venda.", "failure");
            return;
        }
        adicionarSelecao("clienteId", "Cliente", clientes, (item) => item.nome + " (#" + item.id + ")");
        adicionarSelecao("medicamentoId", "Medicamento", disponiveis, (item) =>
            item.nome + " • " + moeda.format(item.preco) + " • " + item.quantidadeEstoque + " em estoque");
        adicionarCampo("quantidade", "Quantidade", "number", { min: 1, max: 2147483647 }, 1);
    } else {
        config.campos.forEach(([nome, rotulo, tipo, opcoes]) =>
            adicionarCampo(nome, rotulo, tipo, opcoes, item?.[nome]));
    }
    editor.showModal();
}

async function excluir(item, botaoExcluir) {
    if (!window.confirm("Excluir " + item.nome + "?")) return;
    botaoExcluir.disabled = true;
    try {
        await api("/medicamentos/" + item.id, "DELETE");
        informar("Medicamento excluído.");
        await carregar();
    } catch (problema) {
        informar(problema.message, "failure");
    } finally {
        botaoExcluir.disabled = false;
    }
}

formulario.addEventListener("submit", async (evento) => {
    evento.preventDefault();
    if (enviando) return;
    const corpo = Object.fromEntries(new FormData(formulario));
    const numericos = ["preco", "quantidadeEstoque", "clienteId", "medicamentoId", "quantidade"];
    Object.keys(corpo).forEach((chave) => {
        corpo[chave] = numericos.includes(chave) ? Number(corpo[chave]) : corpo[chave].trim();
    });
    if (corpo.telefone === "") corpo.telefone = null;

    enviando = true;
    salvar.disabled = true;
    erro.hidden = true;
    try {
        const resultado = await api("/" + pagina + (edicao ? "/" + edicao.id : ""), edicao ? "PUT" : "POST", corpo);
        editor.close();
        informar(pagina === "vendas" ? "Venda registrada. Total: " + moeda.format(resultado.total) : "Registro salvo.");
        await carregar();
    } catch (problema) {
        erro.textContent = problema.message;
        erro.hidden = false;
    } finally {
        salvar.disabled = false;
        enviando = false;
    }
});

editor.addEventListener("cancel", (evento) => {
    if (enviando) evento.preventDefault();
});
["fechar", "cancelar"].forEach((id) => selecionar(id).addEventListener("click", () => {
    if (!enviando) editor.close();
}));
selecionar("novo").addEventListener("click", () => abrirEditor());
busca.addEventListener("input", renderizar);
document.querySelectorAll("[data-page]").forEach((botao) =>
    botao.addEventListener("click", () => mudarPagina(botao.dataset.page)));
mudarPagina(pagina);
