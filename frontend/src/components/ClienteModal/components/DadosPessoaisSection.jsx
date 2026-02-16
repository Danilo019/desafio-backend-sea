import React from 'react';
import { FaUser } from 'react-icons/fa';
import {
  Section,
  SectionTitle,
  Row,
  FormGroup,
  Label,
  Input,
  MaskedInput,
  ErrorText,
} from '../styles';

/**
 * Seção de Dados Pessoais do Cliente
 */
export const DadosPessoaisSection = ({ 
  formData, 
  errors, 
  onChange, 
  disabled 
}) => {
  const today = new Date().toISOString().split('T')[0];

  return (
    <Section>
      <SectionTitle>
        <FaUser /> Dados Pessoais
      </SectionTitle>

      <Row $columns="1fr">
        <FormGroup>
          <Label>
            Nome Completo <span>*</span>
          </Label>
          <Input
            type="text"
            name="nome"
            value={formData.nome}
            onChange={onChange}
            disabled={disabled}
            placeholder="Digite o nome completo"
            $error={errors.nome}
          />
          {errors.nome && <ErrorText>{errors.nome}</ErrorText>}
        </FormGroup>
      </Row>

      <Row $columns="1fr 1fr">
        <FormGroup>
          <Label>
            CPF <span>*</span>
          </Label>
          <MaskedInput
            mask="999.999.999-99"
            name="cpf"
            value={formData.cpf}
            onChange={onChange}
            disabled={disabled}
            placeholder="000.000.000-00"
            $error={errors.cpf}
          />
          {errors.cpf && <ErrorText>{errors.cpf}</ErrorText>}
        </FormGroup>

        <FormGroup>
          <Label>
            Data de Nascimento <span>*</span>
          </Label>
          <Input
            type="date"
            name="dataNascimento"
            value={formData.dataNascimento}
            onChange={onChange}
            disabled={disabled}
            max={today}
            $error={errors.dataNascimento}
          />
          {errors.dataNascimento && <ErrorText>{errors.dataNascimento}</ErrorText>}
        </FormGroup>
      </Row>
    </Section>
  );
};
