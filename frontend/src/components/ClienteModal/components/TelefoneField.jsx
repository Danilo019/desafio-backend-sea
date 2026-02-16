import React from 'react';
import { FaTrash } from 'react-icons/fa';
import {
  ArrayItem,
  FormGroup,
  Label,
  MaskedInput,
  Select,
  Checkbox,
  RemoveButton,
  ErrorText,
} from '../styles';

/**
 * Componente para gerenciar um item de telefone
 */
export const TelefoneField = ({ 
  telefone, 
  index, 
  disabled, 
  onRemove, 
  onChange, 
  onPrincipalChange,
  error 
}) => {
  return (
    <ArrayItem>
      <FormGroup style={{ flex: 2 }}>
        <Label>Número *</Label>
        <MaskedInput
          mask="(99) 99999-9999"
          value={telefone.numero}
          onChange={(e) => onChange(index, 'numero', e.target.value)}
          disabled={disabled}
          placeholder="(00) 00000-0000"
          $error={error}
        />
        {error && <ErrorText>{error}</ErrorText>}
      </FormGroup>

      <FormGroup style={{ flex: 1 }}>
        <Label>Tipo</Label>
        <Select
          value={telefone.tipo}
          onChange={(e) => onChange(index, 'tipo', e.target.value)}
          disabled={disabled}
        >
          <option value="CELULAR">Celular</option>
          <option value="RESIDENCIAL">Residencial</option>
          <option value="COMERCIAL">Comercial</option>
        </Select>
      </FormGroup>

      <FormGroup style={{ flex: 0, minWidth: '80px' }}>
        <Label>Principal</Label>
        <Checkbox
          type="checkbox"
          checked={telefone.principal}
          onChange={() => onPrincipalChange(index)}
          disabled={disabled}
        />
      </FormGroup>

      {index > 0 && !disabled && (
        <RemoveButton
          type="button"
          onClick={() => onRemove(index)}
          title="Remover telefone"
        >
          <FaTrash />
        </RemoveButton>
      )}
    </ArrayItem>
  );
};
