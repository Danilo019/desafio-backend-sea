import { useState, useEffect } from 'react';
import { toast } from 'react-toastify';
import { clienteService } from '../services/clienteService';
import { validateClienteForm } from '../utils/formValidator';

const initialFormState = {
  nome: '',
  cpf: '',
  dataNascimento: '',
  endereco: {
    cep: '',
    logradouro: '',
    numero: '',
    complemento: '',
    bairro: '',
    cidade: '',
    uf: '',
  },
  telefones: [{ numero: '', tipo: 'CELULAR', principal: true }],
  emails: [{ enderecoEmail: '', principal: true }],
};

/**
 * Hook customizado para gerenciar formulário de cliente
 * @param {Object} clienteInicial - Cliente para edição (opcional)
 * @param {Function} onSuccess - Callback de sucesso
 * @param {Function} onClose - Callback para fechar modal
 * @returns {Object} - Estado e funções do formulário
 */
export const useClienteForm = (clienteInicial, onSuccess, onClose) => {
  const [formData, setFormData] = useState(initialFormState);
  const [errors, setErrors] = useState({});
  const [loading, setLoading] = useState(false);

  // Carrega dados do cliente para edição
  useEffect(() => {
    if (clienteInicial) {
      setFormData({
        nome: clienteInicial.nome || '',
        cpf: clienteInicial.cpf || '',
        dataNascimento: clienteInicial.dataNascimento || '',
        endereco: {
          cep: clienteInicial.endereco?.cep || '',
          logradouro: clienteInicial.endereco?.logradouro || '',
          numero: clienteInicial.endereco?.numero || '',
          complemento: clienteInicial.endereco?.complemento || '',
          bairro: clienteInicial.endereco?.bairro || '',
          cidade: clienteInicial.endereco?.cidade || '',
          uf: clienteInicial.endereco?.uf || '',
        },
        telefones: clienteInicial.telefones?.length > 0 
          ? clienteInicial.telefones 
          : [{ numero: '', tipo: 'CELULAR', principal: true }],
        emails: clienteInicial.emails?.length > 0 
          ? clienteInicial.emails 
          : [{ enderecoEmail: '', principal: true }],
      });
    }
  }, [clienteInicial]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
    // Limpa erro do campo quando usuário digita
    if (errors[name]) {
      setErrors(prev => ({ ...prev, [name]: undefined }));
    }
  };

  const handleEnderecoChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      endereco: { ...prev.endereco, [name]: value }
    }));
    // Limpa erro do campo quando usuário digita
    if (errors[name]) {
      setErrors(prev => ({ ...prev, [name]: undefined }));
    }
  };

  const addTelefone = () => {
    setFormData(prev => ({
      ...prev,
      telefones: [...prev.telefones, { numero: '', tipo: 'CELULAR', principal: false }]
    }));
  };

  const removeTelefone = (index) => {
    setFormData(prev => ({
      ...prev,
      telefones: prev.telefones.filter((_, i) => i !== index)
    }));
  };

  const handleTelefoneChange = (index, field, value) => {
    setFormData(prev => ({
      ...prev,
      telefones: prev.telefones.map((tel, i) => 
        i === index ? { ...tel, [field]: value } : tel
      )
    }));
  };

  const handleTelefonePrincipalChange = (index) => {
    setFormData(prev => ({
      ...prev,
      telefones: prev.telefones.map((tel, i) => ({
        ...tel,
        principal: i === index
      }))
    }));
  };

  const addEmail = () => {
    setFormData(prev => ({
      ...prev,
      emails: [...prev.emails, { enderecoEmail: '', principal: false }]
    }));
  };

  const removeEmail = (index) => {
    setFormData(prev => ({
      ...prev,
      emails: prev.emails.filter((_, i) => i !== index)
    }));
  };

  const handleEmailChange = (index, field, value) => {
    setFormData(prev => ({
      ...prev,
      emails: prev.emails.map((email, i) => 
        i === index ? { ...email, [field]: value } : email
      )
    }));
  };

  const handleEmailPrincipalChange = (index) => {
    setFormData(prev => ({
      ...prev,
      emails: prev.emails.map((email, i) => ({
        ...email,
        principal: i === index
      }))
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    // Valida formulário
    const validationErrors = validateClienteForm(formData);
    if (Object.keys(validationErrors).length > 0) {
      setErrors(validationErrors);
      toast.error('Corrija os erros do formulário');
      return;
    }

    setLoading(true);

    try {
      if (clienteInicial?.id) {
        await clienteService.update(clienteInicial.id, formData);
        toast.success('Cliente atualizado com sucesso!');
      } else {
        await clienteService.create(formData);
        toast.success('Cliente cadastrado com sucesso!');
      }
      
      if (onSuccess) onSuccess();
      if (onClose) onClose();
    } catch (error) {
      if (error.response?.status === 400) {
        const serverErrors = {};
        const errorMessage = error.response.data.message || error.response.data.error;
        
        if (errorMessage) {
          if (errorMessage.includes('CPF')) {
            serverErrors.cpf = errorMessage;
          } else if (errorMessage.includes('CEP')) {
            serverErrors.cep = errorMessage;
          } else {
            toast.error(errorMessage);
          }
        }
        
        setErrors(serverErrors);
      } else {
        toast.error('Erro ao salvar cliente');
      }
      console.error(error);
    } finally {
      setLoading(false);
    }
  };

  const updateEndereco = (enderecoData) => {
    setFormData(prev => ({
      ...prev,
      endereco: { ...prev.endereco, ...enderecoData }
    }));
  };

  return {
    formData,
    errors,
    loading,
    handleChange,
    handleEnderecoChange,
    handleSubmit,
    updateEndereco,
    telefones: {
      add: addTelefone,
      remove: removeTelefone,
      handleChange: handleTelefoneChange,
      handlePrincipalChange: handleTelefonePrincipalChange,
    },
    emails: {
      add: addEmail,
      remove: removeEmail,
      handleChange: handleEmailChange,
      handlePrincipalChange: handleEmailPrincipalChange,
    },
  };
};
