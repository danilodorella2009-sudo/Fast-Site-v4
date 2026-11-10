// Funções usadas pelas telas de CRUD (Parte 4): Veículos e Ordens de Serviço

const $ = id => document.getElementById(id);

// Chama o Java. 401 = sessão expirou -> login; 403 = não é admin -> início
async function api(url, opcoes = {}) {
    if (opcoes.body && typeof opcoes.body !== "string") {
        opcoes.body = JSON.stringify(opcoes.body);
        opcoes.headers = { "Content-Type": "application/json" };
    }
    const r = await fetch(url, opcoes);
    if (r.status === 401) { window.location.href = "/index.html"; throw new Error("redirecionado"); }
    if (r.status === 403) { window.location.href = "/home.html"; throw new Error("redirecionado"); }
    const dados = await r.json().catch(() => ({}));
    if (!r.ok) throw new Error(dados.mensagem || "Erro " + r.status);
    return dados;
}

// Aviso flutuante: tipo "sucesso" (verde) ou "erro" (vermelho)
function toast(texto, tipo = "sucesso") {
    const t = $("toast");
    t.textContent = (tipo === "erro" ? "✖ " : "✔ ") + texto;
    t.className = "toast mostrar " + (tipo === "erro" ? "toast-erro" : "");
    clearTimeout(t._timer);
    t._timer = setTimeout(() => t.classList.remove("mostrar"), 3500);
}

// Janela de confirmação (modal). Devolve uma Promise: true = confirmou, false = cancelou
function confirmar(titulo, texto) {
    return new Promise(resolve => {
        const fundo = document.createElement("div");
        fundo.className = "modal-fundo";
        fundo.innerHTML = `
            <div class="modal" role="dialog" aria-modal="true" aria-labelledby="modalTitulo">
                <h3 id="modalTitulo"></h3>
                <p></p>
                <div class="modal-botoes">
                    <button class="btn btn-claro" data-nao>Cancelar</button>
                    <button class="btn btn-perigo-cheio" data-sim>Sim, excluir</button>
                </div>
            </div>`;
        fundo.querySelector("h3").textContent = titulo;
        fundo.querySelector("p").textContent = texto;
        const fechar = valor => { fundo.remove(); document.removeEventListener("keydown", tecla); resolve(valor); };
        const tecla = e => { if (e.key === "Escape") fechar(false); };
        fundo.querySelector("[data-sim]").onclick = () => fechar(true);
        fundo.querySelector("[data-nao]").onclick = () => fechar(false);
        fundo.addEventListener("click", e => { if (e.target === fundo) fechar(false); });
        document.addEventListener("keydown", tecla);
        document.body.appendChild(fundo);
        fundo.querySelector("[data-nao]").focus();
    });
}

// Desafio 2: exporta a tabela para um arquivo .csv (abre no Excel)
function exportarCSV(nomeArquivo, cabecalho, linhas) {
    const celula = v => '"' + String(v ?? "").replace(/"/g, '""') + '"';
    const conteudo = [cabecalho, ...linhas].map(l => l.map(celula).join(";")).join("\r\n");
    // ﻿ faz o Excel reconhecer os acentos (UTF-8)
    const blob = new Blob(["﻿" + conteudo], { type: "text/csv;charset=utf-8" });
    const link = document.createElement("a");
    link.href = URL.createObjectURL(blob);
    link.download = nomeArquivo;
    link.click();
    URL.revokeObjectURL(link.href);
}

// 1234.5 -> "R$ 1.234,50"
function moeda(valor) {
    return Number(valor).toLocaleString("pt-BR", { style: "currency", currency: "BRL" });
}

// Ignora acentos e maiúsculas na busca
function normalizar(texto) {
    return String(texto ?? "").normalize("NFD").replace(/[̀-ͯ]/g, "").toLowerCase();
}

// Mensagem de erro dentro do formulário
function erroForm(texto) {
    const el = $("msgForm");
    el.textContent = texto;
    el.className = texto ? "alerta erro" : "alerta";
}
