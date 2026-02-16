import api from './api';

export const clienteService = {
  async getAll(page = 0, size = 10) {
    try {
      const response = await api.get(`/clientes?page=${page}&size=${size}`);
      return response.data;
    } catch (error) {
      console.error('Erro ao buscar clientes:', error);
      throw error;
    }
  },

  async getById(id) {
    try {
      const response = await api.get(`/clientes/${id}`);
      return response.data;
    } catch (error) {
      console.error('Erro ao buscar cliente:', error);
      throw error;
    }
  },

  async create(cliente) {
    try {
      const response = await api.post('/clientes', cliente);
      return response.data;
    } catch (error) {
      console.error('Erro ao criar cliente:', error);
      throw error;
    }
  },

  async update(id, cliente) {
    try {
      const response = await api.put(`/clientes/${id}`, cliente);
      return response.data;
    } catch (error) {
      console.error('Erro ao atualizar cliente:', error);
      throw error;
    }
  },

  async delete(id) {
    try {
      await api.delete(`/clientes/${id}`);
      return true;
    } catch (error) {
      console.error('Erro ao deletar cliente:', error);
      throw error;
    }
  },

  async searchByCpf(cpf) {
    try {
      const response = await api.get(`/clientes/cpf/${cpf}`);
      return response.data;
    } catch (error) {
      console.error('Erro ao buscar cliente por CPF:', error);
      throw error;
    }
  },

  async consultarCep(cep) {
    try {
      const response = await api.get(`/cep/${cep}`);
      return response.data;
    } catch (error) {
      console.error('Erro ao consultar CEP:', error);
      throw error;
    }
  },
};
