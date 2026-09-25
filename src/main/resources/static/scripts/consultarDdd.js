const formulario = document.getElementById("consulta-form");
const resultado = document.getElementById("resultado");
const ddd = document.getElementById("ddd");
const dddResultado = document.getElementById("ddd-resultado");
const estado = document.getElementById("estado");
const quantidadeCidades = document.getElementById("quantidade-cidades");
const cidades = document.getElementById("cidades");
let cidadesConsultadas = [];

formulario.addEventListener("submit", async (event) => {
    event.preventDefault();

    const valorDdd = ddd.value.trim();

    try {
        const response = await fetch(`/api/ddd/v1/${encodeURIComponent(valorDdd)}`);

        if (!response.ok) {
            throw new Error(`Erro ao consultar o DDD: ${response.status}`);
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
