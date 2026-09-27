const formulario = document.getElementById("consulta-form");
const resultado = document.getElementById("resultado");
const ddd = document.getElementById("ddd");
const dddResultado = document.getElementById("ddd-resultado");
const estado = document.getElementById("estado");
const quantidadeCidades = document.getElementById("quantidade-cidades");
const cidades = document.getElementById("cidades");
const erro = document.getElementById("erro");
let cidadesConsultadas = [];

formulario.addEventListener("submit", async (event) => {
    event.preventDefault();

    const valorDdd = ddd.value.trim();
    erro.classList.add("d-none");
    resultado.classList.add("d-none");

    try {
        const response = await fetch(`/api/ddd/v1/${encodeURIComponent(valorDdd)}`);

        if (!response.ok) {
            const erroResponse = await response.json().catch(() => null);
            throw new Error(
                erroResponse?.mensagem || "Não foi possível concluir a consulta."
            );
        }

        const dados = await response.json();

        dddResultado.textContent = `DDD ${valorDdd}`;
        estado.textContent = dados.state;
        quantidadeCidades.textContent = `${dados.cities.length} municípios`;
        cidadesConsultadas = dados.cities;
        renderizarCidades();
        resultado.classList.remove("d-none");
    } catch (error) {
        console.error(error);
        erro.textContent = error.message || "Não foi possível concluir a consulta.";
        erro.classList.remove("d-none");
    }
});

window.addEventListener("resize", () => {
    if (cidadesConsultadas.length > 0) {
        renderizarCidades();
    }
});

function renderizarCidades() {
    const colunas = window.innerWidth < 768 ? 2 : 3;
    const linhas = [];

    for (let indice = 0; indice < cidadesConsultadas.length; indice += colunas) {
        const linha = document.createElement("tr");
        cidadesConsultadas.slice(indice, indice + colunas).forEach((cidade) => {
            const celula = document.createElement("td");
            celula.textContent = cidade;
            linha.appendChild(celula);
        });
        linhas.push(linha);
    }

    cidades.replaceChildren(...linhas);
}
