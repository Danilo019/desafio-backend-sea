import { useState } from 'react';
import { clienteService } from '../services/clienteService';
import { toast } from 'react-toastify';

/**
 * Hook customizado para buscar endereço via CEP
 * @returns {Object} - { consultarCEP, loading }
 */
export const useViaCEP = () => {
  const [loading, setLoading] = useState(false);

  const consultarCEP = async (cep, onSuccess) => {
    const cepLimpo = cep.replace(/\D/g, '');
    
    if (cepLimpo.length !== 8) {
      toast.error('CEP deve ter 8 dígitos');
      return;
    }

    setLoading(true);
    try {
      const dados = await clienteService.consultarCep(cepLimpo);
      
      if (dados.erro) {
        toast.error('CEP não encontrado');
        return;
      }

      if (onSuccess) {
        onSuccess({
          logradouro: dados.logradouro || '',
          bairro: dados.bairro || '',
          cidade: dados.localidade || '',
          uf: dados.uf || '',
        });
      }

      toast.success('CEP encontrado!');
    } catch (error) {
      toast.error('Erro ao consultar CEP');
      console.error(error);
    } finally {
      setLoading(false);
    }
  };

  return { consultarCEP, loading };
};
