import React, { useState } from 'react';
import { FaPlus, FaSearch, FaUsers } from 'react-icons/fa';
import { toast } from 'react-toastify';
import Layout from '../../components/Layout';
import ClienteModal from '../../components/ClienteModal';
import { useClientes } from '../../hooks/useClientes';
import { ClienteTableRow } from './components/ClienteTableRow';
import { PaginationControls } from './components/PaginationControls';
import {
  Container,
  ActionBar,
  SearchWrapper,
  SearchInput,
  Button,
  Table,
  TableHeader,
  TableCell,
  Loading,
  EmptyState,
} from './styles';

/**
 * Página de listagem e gerenciamento de clientes
 */
function Clientes() {
  const {
    clientes,
    loading,
    page,
    totalPages,
    searchTerm,
    isAdmin,
    setSearchTerm,
    handleDelete,
    handlePageChange,
    reloadClientes,
  } = useClientes();

  const [modalOpen, setModalOpen] = useState(false);
  const [editingCliente, setEditingCliente] = useState(null);
  const [viewMode, setViewMode] = useState(false);

  const handleAdd = () => {
    setEditingCliente(null);
    setViewMode(false);
    setModalOpen(true);
  };

  const handleEdit = (cliente) => {
    if (!isAdmin) {
      toast.warning('Apenas administradores podem editar clientes');
      return;
    }
    setEditingCliente(cliente);
    setViewMode(false);
    setModalOpen(true);
  };

  const handleView = (cliente) => {
    setEditingCliente(cliente);
    setViewMode(true);
    setModalOpen(true);
  };

  const handleCloseModal = () => {
    setModalOpen(false);
    setEditingCliente(null);
    setViewMode(false);
  };

  const handleSaveSuccess = () => {
    reloadClientes();
    handleCloseModal();
  };

  return (
    <Layout>
      <Container>
        {/* Barra de Ações */}
        <ActionBar>
          <SearchWrapper>
            <FaSearch />
            <SearchInput
              type="text"
              placeholder="Buscar por nome, CPF ou e-mail..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
            />
          </SearchWrapper>
          
          {isAdmin && (
            <Button $variant="primary" onClick={handleAdd}>
              <FaPlus /> Novo Cliente
            </Button>
          )}
        </ActionBar>

        {/* Tabela de Clientes */}
        {loading ? (
          <Loading>
            <div className="spinner" />
            <p>Carregando clientes...</p>
          </Loading>
        ) : clientes.length === 0 ? (
          <EmptyState>
            <FaUsers />
            <p>
              {searchTerm 
                ? 'Nenhum cliente encontrado com esse critério de busca' 
                : 'Nenhum cliente cadastrado ainda'}
            </p>
          </EmptyState>
        ) : (
          <>
            <Table>
              <TableHeader>
                <TableCell>Nome</TableCell>
                <TableCell>CPF</TableCell>
                <TableCell>E-mail</TableCell>
                <TableCell>Telefone</TableCell>
                <TableCell>Ações</TableCell>
              </TableHeader>

              {clientes.map((cliente) => (
                <ClienteTableRow
                  key={cliente.id}
                  cliente={cliente}
                  isAdmin={isAdmin}
                  onView={handleView}
                  onEdit={handleEdit}
                  onDelete={handleDelete}
                />
              ))}
            </Table>

            {/* Paginação */}
            <PaginationControls
              currentPage={page}
              totalPages={totalPages}
              onPageChange={handlePageChange}
            />
          </>
        )}

        {/* Modal de Cliente */}
        {modalOpen && (
          <ClienteModal
            cliente={editingCliente}
            viewMode={viewMode}
            onClose={handleCloseModal}
            onSave={handleSaveSuccess}
          />
        )}
      </Container>
    </Layout>
  );
}

export default Clientes;
