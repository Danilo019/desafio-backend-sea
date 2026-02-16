import styled from 'styled-components';
import InputMask from 'react-input-mask';
import { colors, borderRadius, spacing, shadows } from '../../styles/theme';

export const Overlay = styled.div`
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: ${colors.overlay};
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  animation: fadeIn 0.3s ease-in-out;
  padding: ${spacing.lg};
  overflow-y: auto;

  @keyframes fadeIn {
    from { opacity: 0; }
    to { opacity: 1; }
  }
`;

export const Modal = styled.div`
  background: ${colors.cardBg};
  border-radius: ${borderRadius.xl};
  width: 100%;
  max-width: 800px;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: ${shadows.xl};
  animation: slideIn 0.3s ease-in-out;
  
  @keyframes slideIn {
    from {
      transform: translateY(-50px);
      opacity: 0;
    }
    to {
      transform: translateY(0);
      opacity: 1;
    }
  }
`;

export const Header = styled.div`
  padding: ${spacing.xl};
  border-bottom: 1px solid ${colors.border};
  display: flex;
  justify-content: space-between;
  align-items: center;
  position: sticky;
  top: 0;
  background: ${colors.cardBg};
  z-index: 10;
`;

export const Title = styled.h2`
  font-size: 24px;
  color: ${colors.textPrimary};
`;

export const CloseButton = styled.button`
  padding: ${spacing.sm};
  background: transparent;
  border: none;
  color: ${colors.textSecondary};
  font-size: 24px;
  cursor: pointer;
  transition: all 0.3s ease;
  border-radius: ${borderRadius.sm};

  &:hover {
    background: ${colors.primaryLight};
    color: ${colors.textPrimary};
  }
`;

export const Content = styled.div`
  padding: ${spacing.xl};
`;

export const Form = styled.form`
  display: flex;
  flex-direction: column;
  gap: ${spacing.xl};
`;

export const Section = styled.div`
  background: ${colors.inputBg};
  padding: ${spacing.lg};
  border-radius: ${borderRadius.md};
`;

export const SectionTitle = styled.h3`
  font-size: 16px;
  color: ${colors.accent};
  margin-bottom: ${spacing.lg};
  display: flex;
  align-items: center;
  gap: ${spacing.sm};
`;

export const Row = styled.div`
  display: grid;
  grid-template-columns: ${props => props.$columns || '1fr'};
  gap: ${spacing.md};
  margin-bottom: ${spacing.md};

  &:last-child {
    margin-bottom: 0;
  }
`;

export const FormGroup = styled.div`
  display: flex;
  flex-direction: column;
  gap: ${spacing.sm};
`;

export const Label = styled.label`
  font-size: 14px;
  font-weight: 500;
  color: ${colors.textPrimary};
  
  span {
    color: ${colors.error};
  }
`;

export const Input = styled.input`
  padding: ${spacing.md};
  background: ${colors.background};
  border: 2px solid ${props => props.$error ? colors.error : colors.inputBorder};
  border-radius: ${borderRadius.md};
  color: ${colors.textPrimary};
  font-size: 14px;
  transition: all 0.3s ease;

  &:focus {
    outline: none;
    border-color: ${colors.accent};
    box-shadow: 0 0 0 3px rgba(255, 152, 0, 0.1);
  }

  &:disabled {
    opacity: 0.6;
    cursor: not-allowed;
  }

  &::placeholder {
    color: ${colors.textMuted};
  }
`;

export const MaskedInput = styled(InputMask)`
  padding: ${spacing.md};
  background: ${colors.background};
  border: 2px solid ${props => props.$error ? colors.error : colors.inputBorder};
  border-radius: ${borderRadius.md};
  color: ${colors.textPrimary};
  font-size: 14px;
  transition: all 0.3s ease;

  &:focus {
    outline: none;
    border-color: ${colors.accent};
    box-shadow: 0 0 0 3px rgba(255, 152, 0, 0.1);
  }

  &:disabled {
    opacity: 0.6;
    cursor: not-allowed;
  }

  &::placeholder {
    color: ${colors.textMuted};
  }
`;

export const Select = styled.select`
  padding: ${spacing.md};
  background: ${colors.background};
  border: 2px solid ${colors.inputBorder};
  border-radius: ${borderRadius.md};
  color: ${colors.textPrimary};
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s ease;

  &:focus {
    outline: none;
    border-color: ${colors.accent};
    box-shadow: 0 0 0 3px rgba(255, 152, 0, 0.1);
  }

  &:disabled {
    opacity: 0.6;
    cursor: not-allowed;
  }
`;

export const Checkbox = styled.input`
  width: 18px;
  height: 18px;
  cursor: pointer;
  accent-color: ${colors.accent};
`;

export const ErrorText = styled.span`
  font-size: 12px;
  color: ${colors.error};
  display: block;
  margin-top: 4px;
`;

export const ArraySection = styled.div`
  margin-top: ${spacing.md};
`;

export const ArrayItem = styled.div`
  display: flex;
  gap: ${spacing.md};
  margin-bottom: ${spacing.md};
  padding: ${spacing.md};
  background: ${colors.background};
  border-radius: ${borderRadius.md};
  align-items: flex-start;
`;

export const RemoveButton = styled.button`
  padding: ${spacing.sm};
  background: ${colors.error};
  border: none;
  border-radius: ${borderRadius.sm};
  color: ${colors.white};
  cursor: pointer;
  transition: all 0.3s ease;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;

  &:hover {
    background: #d32f2f;
    transform: scale(1.05);
  }

  &:disabled {
    opacity: 0.5;
    cursor: not-allowed;
  }
`;

export const AddButton = styled.button`
  padding: ${spacing.md};
  background: ${colors.primaryLight};
  border: none;
  border-radius: ${borderRadius.md};
  color: ${colors.white};
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: ${spacing.sm};
  margin-top: ${spacing.sm};

  &:hover {
    background: ${colors.primary};
    transform: translateY(-2px);
  }
`;

export const Footer = styled.div`
  padding: ${spacing.xl};
  border-top: 1px solid ${colors.border};
  display: flex;
  justify-content: flex-end;
  gap: ${spacing.md};
  position: sticky;
  bottom: 0;
  background: ${colors.cardBg};
`;

export const Button = styled.button`
  padding: ${spacing.md} ${spacing.xl};
  background: ${props => props.$variant === 'primary' 
    ? `linear-gradient(135deg, ${colors.accent} 0%, ${colors.accentLight} 100%)`
    : colors.primaryLight};
  border: none;
  border-radius: ${borderRadius.md};
  color: ${colors.white};
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
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

export const LoadingCep = styled.span`
  font-size: 12px;
  color: ${colors.accent};
  margin-top: -${spacing.sm};
`;
