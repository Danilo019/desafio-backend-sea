import React from 'react';
import { Pagination, PageButton } from '../styles';

/**
 * Componente de paginação
 */
export const PaginationControls = ({ currentPage, totalPages, onPageChange }) => {
  if (totalPages <= 1) return null;

  const pages = [];
  const maxVisiblePages = 5;
  
  let startPage = Math.max(0, currentPage - Math.floor(maxVisiblePages / 2));
  let endPage = Math.min(totalPages - 1, startPage + maxVisiblePages - 1);
  
  if (endPage - startPage < maxVisiblePages - 1) {
    startPage = Math.max(0, endPage - maxVisiblePages + 1);
  }

  for (let i = startPage; i <= endPage; i++) {
    pages.push(i);
  }

  return (
    <Pagination>
      <PageButton
        onClick={() => onPageChange(currentPage - 1)}
        disabled={currentPage === 0}
      >
        ← Anterior
      </PageButton>

      {startPage > 0 && (
        <>
          <PageButton onClick={() => onPageChange(0)}>
            1
          </PageButton>
          {startPage > 1 && <span>...</span>}
        </>
      )}

      {pages.map(pageNum => (
        <PageButton
          key={pageNum}
          $active={pageNum === currentPage}
          onClick={() => onPageChange(pageNum)}
        >
          {pageNum + 1}
        </PageButton>
      ))}

      {endPage < totalPages - 1 && (
        <>
          {endPage < totalPages - 2 && <span>...</span>}
          <PageButton onClick={() => onPageChange(totalPages - 1)}>
            {totalPages}
          </PageButton>
        </>
      )}

      <PageButton
        onClick={() => onPageChange(currentPage + 1)}
        disabled={currentPage === totalPages - 1}
      >
        Próxima →
      </PageButton>
    </Pagination>
  );
};
