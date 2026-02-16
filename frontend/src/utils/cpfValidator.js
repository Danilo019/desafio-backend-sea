/**
 * Valida CPF segundo algoritmo dos dígitos verificadores
 * @param {string} cpf - CPF com ou sem máscara
 * @returns {boolean} - true se válido
 */
export const validarCPF = (cpf) => {
  cpf = cpf.replace(/\D/g, '');
  
  // Verifica se tem 11 dígitos e não é sequência repetida
  if (cpf.length !== 11 || /^(\d)\1{10}$/.test(cpf)) {
    return false;
  }

  // Valida primeiro dígito verificador
  let soma = 0;
  for (let i = 1; i <= 9; i++) {
    soma += parseInt(cpf.substring(i - 1, i)) * (11 - i);
  }
  let resto = (soma * 10) % 11;
  if (resto === 10 || resto === 11) resto = 0;
  if (resto !== parseInt(cpf.substring(9, 10))) return false;

  // Valida segundo dígito verificador
  soma = 0;
  for (let i = 1; i <= 10; i++) {
    soma += parseInt(cpf.substring(i - 1, i)) * (12 - i);
  }
  resto = (soma * 10) % 11;
  if (resto === 10 || resto === 11) resto = 0;
  if (resto !== parseInt(cpf.substring(10, 11))) return false;

  return true;
};

/**
 * Formata CPF com máscara
 * @param {string} cpf - CPF sem formatação
 * @returns {string} - CPF formatado (xxx.xxx.xxx-xx)
 */
export const formatarCPF = (cpf) => {
  cpf = cpf.replace(/\D/g, '');
  return cpf.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, '$1.$2.$3-$4');
};

/**
 * Remove máscara do CPF
 * @param {string} cpf - CPF com máscara
 * @returns {string} - CPF sem formatação
 */
export const removerMascaraCPF = (cpf) => {
  return cpf.replace(/\D/g, '');
};
