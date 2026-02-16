import styled from 'styled-components';
import { colors, borderRadius, spacing, shadows } from '../../styles/theme';

export const Container = styled.div`
  animation: fadeIn 0.5s ease-in-out;

  @keyframes fadeIn {
    from { opacity: 0; }
    to { opacity: 1; }
  }
`;

export const ActionBar = styled.div`
  background: ${colors.cardBg};
  border-radius: ${borderRadius.xl};
  padding: ${spacing.xl};
  margin-bottom: ${spacing.xl};
  display: flex;
  gap: ${spacing.md};
  align-items: center;
  flex-wrap: wrap;
  box-shadow: ${shadows.md};
`;

export const SearchWrapper = styled.div`
  position: relative;
  flex: 1;
  
  svg {
    position: absolute;
    left: ${spacing.md};
    top: 50%;
    transform: translateY(-50%);
    color: ${colors.textSecondary};
  }
`;

export const SearchInput = styled.input`
  flex: 1;
  min-width: 250px;
  padding: ${spacing.md} ${spacing.md} ${spacing.md} 40px;
  background: ${colors.inputBg};
  border: 2px solid ${colors.inputBorder};
  border-radius: ${borderRadius.md};
  color: ${colors.textPrimary};
  font-size: 14px;

  &:focus {
    outline: none;
    border-color: ${colors.accent};
  }

  &::placeholder {
    color: ${colors.textMuted};
  }
`;

export const Button = styled.button`
  padding: ${spacing.md} ${spacing.lg};
  background: ${props => props.$variant === 'primary' 
    ? `linear-gradient(135deg, ${colors.accent} 0%, ${colors.accentLight} 100%)`
    : props.$variant === 'danger'
    ? colors.error
    : colors.primaryLight};
  border: none;
  border-radius: ${borderRadius.md};
  color: ${colors.white};
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: ${spacing.sm};
  box-shadow: ${props => props.$variant === 'primary' ? '0 4px 12px rgba(255, 152, 0, 0.3)' : 'none'};

  &:hover {
    transform: translateY(-2px);
    box-shadow: ${props => props.$variant === 'primary' 
      ? '0 6px 16px rgba(255, 152, 0, 0.4)'
      : '0 4px 12px rgba(0, 0, 0, 0.2)'};
  }

  &:disabled {
    opacity: 0.5;
    cursor: not-allowed;
    transform: none;
  }
`;

export const Table = styled.div`
  background: ${colors.cardBg};
  border-radius: ${borderRadius.xl};
  overflow: hidden;
  box-shadow: ${shadows.md};
`;

export const TableHeader = styled.div`
  display: grid;
  grid-template-columns: 2fr 1.5fr 1.5fr 1fr 120px;
  padding: ${spacing.lg};
  background: ${colors.primaryLight};
  font-weight: 600;
  color: ${colors.textPrimary};
  font-size: 14px;
`;

export const TableRow = styled.div`
  display: grid;
  grid-template-columns: 2fr 1.5fr 1.5fr 1fr 120px;
  padding: ${spacing.lg};
  border-bottom: 1px solid ${colors.border};
  transition: all 0.3s ease;
  align-items: center;

  &:hover {
    background: ${colors.primaryLight}30;
  }

  &:last-child {
    border-bottom: none;
  }
`;

export const TableCell = styled.div`
  color: ${colors.textPrimary};
  font-size: 14px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
`;

export const Actions = styled.div`
  display: flex;
  gap: ${spacing.sm};
`;

export const IconButton = styled.button`
  padding: ${spacing.sm};
  background: ${props => 
    props.$variant === 'view' ? colors.primaryLight :
    props.$variant === 'edit' ? colors.accent :
    props.$variant === 'delete' ? colors.error :
    colors.primaryLight
  };
  border: none;
  border-radius: ${borderRadius.sm};
  color: ${colors.white};
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;

  &:hover {
    transform: scale(1.1);
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
  }

  &:disabled {
    opacity: 0.5;
    cursor: not-allowed;
    transform: none;
  }
`;

export const Pagination = styled.div`
  display: flex;
  justify-content: center;
  align-items: center;
  gap: ${spacing.md};
  padding: ${spacing.xl};
  background: ${colors.cardBg};
  border-radius: ${borderRadius.xl};
  margin-top: ${spacing.xl};
  box-shadow: ${shadows.md};
`;

export const PageButton = styled.button`
  padding: ${spacing.sm} ${spacing.md};
  background: ${props => props.$active ? colors.accent : colors.primaryLight};
  border: none;
  border-radius: ${borderRadius.sm};
  color: ${colors.white};
  font-weight: ${props => props.$active ? '600' : '400'};
  cursor: pointer;
  transition: all 0.3s ease;
  min-width: 40px;

  &:hover:not(:disabled) {
    background: ${props => props.$active ? colors.accent : colors.primaryLight};
    transform: translateY(-2px);
  }

  &:disabled {
    opacity: 0.5;
    cursor: not-allowed;
  }
`;

export const Loading = styled.div`
  padding: ${spacing.xxl};
  text-align: center;
  color: ${colors.textSecondary};
  
  .spinner {
    width: 40px;
    height: 40px;
    border: 4px solid ${colors.primaryLight};
    border-top-color: ${colors.accent};
    border-radius: 50%;
    animation: spin 1s linear infinite;
    margin: 0 auto ${spacing.md};
  }

  @keyframes spin {
    to { transform: rotate(360deg); }
  }
`;

export const EmptyState = styled.div`
  padding: ${spacing.xxl};
  text-align: center;
  color: ${colors.textSecondary};
  
  svg {
    font-size: 48px;
    margin-bottom: ${spacing.md};
    opacity: 0.5;
  }
`;
