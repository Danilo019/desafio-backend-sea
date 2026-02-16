import { useState, useEffect } from 'react';
import { toast } from 'react-toastify';
import { clienteService } from '../services/clienteService';
import { authService } from '../services/authService';

/**
 * Hook customizado para gerenciar lista de clientes
 * @returns {Object} - Estado e funções para gerenciar clientes
 */
export const useClientes = () => {
  const [clientes, setClientes] = useState([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [searchTerm, setSearchTerm] = useState('');
  const isAdmin = authService.isAdmin();

  // Carrega clientes quando a página muda
  useEffect(() => {
    loadClientes();
  }, [page]);

  const loadClientes = async () => {
    try {
      setLoading(true);
      const response = await clienteService.getAll(page, 10);
      setClientes(response.content || []);
      setTotalPages(response.totalPages || 0);
    } catch (error) {
      toast.error('Erro ao carregar clientes');
      console.error(error);
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (id) => {
    if (!isAdmin) {
      toast.warning('Apenas administradores podem excluir clientes');
      return;
    }

    if (!window.confirm('Tem certeza que deseja excluir este cliente?')) {
      return;
    }

    try {
      await clienteService.delete(id);
      toast.success('Cliente excluído com sucesso!');
      loadClientes();
    } catch (error) {
      toast.error('Erro ao excluir cliente');
      console.error(error);
    }
  };

  const handlePageChange = (newPage) => {
    if (newPage >= 0 && newPage < totalPages) {
      setPage(newPage);
    }
  };

  // Filtra clientes localmente baseado no termo de busca
  const filteredClientes = clientes.filter(cliente => 
    !searchTerm || 
    cliente.nome.toLowerCase().includes(searchTerm.toLowerCase()) ||
    cliente.cpf.includes(searchTerm) ||
    cliente.emails?.some(e => e.enderecoEmail.toLowerCase().includes(searchTerm.toLowerCase()))
  );

  return {
    clientes: filteredClientes,
    loading,
    page,
    totalPages,
    searchTerm,
    isAdmin,
    setSearchTerm,
    handleDelete,
    handlePageChange,
    reloadClientes: loadClientes,
  };
};
