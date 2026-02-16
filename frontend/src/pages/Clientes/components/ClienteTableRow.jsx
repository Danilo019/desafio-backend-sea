import React from 'react';
import { FaEdit, FaTrash, FaEye } from 'react-icons/fa';
import { TableRow, TableCell, Actions, IconButton } from '../styles';

/**
 * Componente para exibir uma linha da tabela de clientes
 */
export const ClienteTableRow = ({ 
  cliente, 
  isAdmin, 
  onView, 
  onEdit, 
  onDelete 
}) => {
  const emailPrincipal = cliente.emails?.find(e => e.principal)?.enderecoEmail || 
                         cliente.emails?.[0]?.enderecoEmail || 
                         'N/A';

  const telefonePrincipal = cliente.telefones?.find(t => t.principal)?.numero || 
                            cliente.telefones?.[0]?.numero || 
                            'N/A';

  return (
    <TableRow>
      <TableCell title={cliente.nome}>{cliente.nome}</TableCell>
      <TableCell title={cliente.cpf}>{cliente.cpf}</TableCell>
      <TableCell title={emailPrincipal}>{emailPrincipal}</TableCell>
      <TableCell title={telefonePrincipal}>{telefonePrincipal}</TableCell>
      <Actions>
        <IconButton 
          onClick={() => onView(cliente)} 
          $variant="view"
          title="Visualizar"
        >
          <FaEye />
        </IconButton>
        {isAdmin && (
          <>
            <IconButton 
              onClick={() => onEdit(cliente)} 
              $variant="edit"
              title="Editar"
            >
              <FaEdit />
            </IconButton>
            <IconButton 
              onClick={() => onDelete(cliente.id)} 
              $variant="delete"
              title="Excluir"
            >
              <FaTrash />
            </IconButton>
          </>
        )}
      </Actions>
    </TableRow>
  );
};
