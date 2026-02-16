import React from 'react';
import { FaTrash } from 'react-icons/fa';
import {
  ArrayItem,
  FormGroup,
  Label,
  Input,
  Checkbox,
  RemoveButton,
  ErrorText,
} from '../styles';

/**
 * Componente para gerenciar um item de email
 */
export const EmailField = ({ 
  email, 
  index, 
  disabled, 
  onRemove, 
  onChange, 
  onPrincipalChange,
  error 
}) => {
  return (
    <ArrayItem>
      <FormGroup style={{ flex: 3 }}>
        <Label>E-mail *</Label>
        <Input
          type="email"
          value={email.enderecoEmail}
          onChange={(e) => onChange(index, 'enderecoEmail', e.target.value)}
          disabled={disabled}
          placeholder="exemplo@email.com"
          $error={error}
        />
        {error && <ErrorText>{error}</ErrorText>}
      </FormGroup>

      <FormGroup style={{ flex: 0, minWidth: '80px' }}>
        <Label>Principal</Label>
        <Checkbox
          type="checkbox"
          checked={email.principal}
          onChange={() => onPrincipalChange(index)}
          disabled={disabled}
        />
      </FormGroup>

      {index > 0 && !disabled && (
        <RemoveButton
          type="button"
          onClick={() => onRemove(index)}
          title="Remover e-mail"
        >
          <FaTrash />
        </RemoveButton>
      )}
    </ArrayItem>
  );
};
