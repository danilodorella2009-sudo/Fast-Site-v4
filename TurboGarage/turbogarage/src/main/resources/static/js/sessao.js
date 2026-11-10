// Funções usadas pelas telas que exigem login (home e cadastros)

// Pergunta ao Java quem está logado. Se ninguém, volta para o login.
// Devolve { email, nome, admin }
async function exigirLogin() {
    const resposta = await fetch("/api/usuario");
    if (!resposta.ok) {
        window.location.href = "/index.html";
        return null;
    }
    const usuario = await resposta.json();
    document.querySelectorAll("[data-nome-logado]").forEach(el => el.textContent = usuario.nome);
    document.querySelectorAll("[data-email-logado]").forEach(el => el.textContent = usuario.email);
    // Itens marcados com data-so-admin só aparecem para o administrador
    document.querySelectorAll("[data-so-admin]").forEach(el => el.hidden = !usuario.admin);
    document.querySelectorAll("[data-so-cliente]").forEach(el => el.hidden = usuario.admin);
    document.body.classList.remove("oculto");
    return usuario;
}

// Só para a tela de cadastros salvos: quem não é admin volta para o início
async function exigirAdmin() {
    const usuario = await exigirLogin();
    if (usuario && !usuario.admin) {
        window.location.href = "/home.html";
        return null;
    }
    return usuario;
}

// Botão Sair: apaga a sessão e volta para o login
function ligarBotaoSair() {
    const btn = document.getElementById("btnSair");
    if (!btn) return;
    btn.addEventListener("click", async () => {
        await fetch("/api/logout", { method: "POST" });
        window.location.href = "/index.html";
    });
}
