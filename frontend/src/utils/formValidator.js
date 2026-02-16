import { validarCPF } from './cpfValidator';

/**
 * Valida todos os campos do formulário de cliente
 * @param {Object} formData - Dados do formulário
 * @returns {Object} - Objeto com erros encontrados
 */
export const validateClienteForm = (formData) => {
  const errors = {};

  // Validações de dados pessoais
  if (!formData.nome?.trim()) {
    errors.nome = 'Nome é obrigatório';
  }

  if (!formData.cpf?.trim()) {
    errors.cpf = 'CPF é obrigatório';
  } else if (!validarCPF(formData.cpf)) {
    errors.cpf = 'CPF inválido';
  }

  if (!formData.dataNascimento) {
    errors.dataNascimento = 'Data de nascimento é obrigatória';
  }

  // Validações de endereço
  if (!formData.endereco?.cep?.trim()) {
    errors.cep = 'CEP é obrigatório';
  }

  if (!formData.endereco?.logradouro?.trim()) {
    errors.logradouro = 'Logradouro é obrigatório';
  }

  if (!formData.endereco?.numero?.trim()) {
    errors.numero = 'Número é obrigatório';
  }

  if (!formData.endereco?.bairro?.trim()) {
    errors.bairro = 'Bairro é obrigatório';
  }

  if (!formData.endereco?.cidade?.trim()) {
    errors.cidade = 'Cidade é obrigatória';
  }

  if (!formData.endereco?.uf?.trim()) {
    errors.uf = 'UF é obrigatório';
  }

  // Validação de telefones
  if (!formData.telefones || formData.telefones.length === 0) {
    errors.telefones = 'Adicione pelo menos um telefone';
  } else {
    formData.telefones.forEach((telefone, index) => {
      const numeroLimpo = telefone.numero.replace(/\D/g, '');
      if (numeroLimpo.length < 10) {
        errors[`telefone_${index}`] = 'Telefone deve ter no mínimo 10 dígitos';
      }
    });
  }

  // Validação de emails
  if (!formData.emails || formData.emails.length === 0) {
    errors.emails = 'Adicione pelo menos um e-mail';
  } else {
    formData.emails.forEach((email, index) => {
      const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
      if (!emailRegex.test(email.enderecoEmail)) {
        errors[`email_${index}`] = 'E-mail inválido';
      }
    });
  }

  return errors;
};

/**
 * Verifica se há pelo menos um telefone marcado como principal
 * @param {Array} telefones - Lista de telefones
 * @returns {boolean}
 */
export const hasMainPhone = (telefones) => {
  return telefones.some(tel => tel.principal);
};

/**
 * Verifica se há pelo menos um email marcado como principal
 * @param {Array} emails - Lista de emails
 * @returns {boolean}
 */
export const hasMainEmail = (emails) => {
  return emails.some(email => email.principal);
};
