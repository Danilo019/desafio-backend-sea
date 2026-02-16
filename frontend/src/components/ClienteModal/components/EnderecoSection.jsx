import React from 'react';
import { FaMapMarkerAlt } from 'react-icons/fa';
import {
  Section,
  SectionTitle,
  Row,
  FormGroup,
  Label,
  Input,
  MaskedInput,
  ErrorText,
  LoadingCep,
} from '../styles';
import { useViaCEP } from '../../../hooks/useViaCEP';

/**
 * Seção de Endereço com integração ViaCEP
 */
export const EnderecoSection = ({ 
  endereco, 
  errors, 
  onChange, 
  onCepComplete,
  disabled 
}) => {
  const { consultarCEP, loading } = useViaCEP();

  const handleCepBlur = () => {
    if (endereco.cep && !disabled) {
      consultarCEP(endereco.cep, (dados) => {
        if (onCepComplete) {
          onCepComplete(dados);
        }
      });
    }
  };

  return (
    <Section>
      <SectionTitle>
        <FaMapMarkerAlt /> Endereço
      </SectionTitle>

      <Row $columns="1fr 2fr">
        <FormGroup>
          <Label>
            CEP <span>*</span>
          </Label>
          <MaskedInput
            mask="99999-999"
            name="cep"
            value={endereco.cep}
            onChange={onChange}
            onBlur={handleCepBlur}
            disabled={disabled}
            placeholder="00000-000"
            $error={errors.cep}
          />
          {loading && <LoadingCep>Buscando CEP...</LoadingCep>}
          {errors.cep && <ErrorText>{errors.cep}</ErrorText>}
        </FormGroup>

        <FormGroup>
          <Label>
            Logradouro <span>*</span>
          </Label>
          <Input
            type="text"
            name="logradouro"
            value={endereco.logradouro}
            onChange={onChange}
            disabled={disabled}
            placeholder="Rua, Avenida, etc."
            $error={errors.logradouro}
          />
          {errors.logradouro && <ErrorText>{errors.logradouro}</ErrorText>}
        </FormGroup>
      </Row>

      <Row $columns="1fr 3fr">
        <FormGroup>
          <Label>
            Número <span>*</span>
          </Label>
          <Input
            type="text"
            name="numero"
            value={endereco.numero}
            onChange={onChange}
            disabled={disabled}
            placeholder="123"
            $error={errors.numero}
          />
          {errors.numero && <ErrorText>{errors.numero}</ErrorText>}
        </FormGroup>

        <FormGroup>
          <Label>Complemento</Label>
          <Input
            type="text"
            name="complemento"
            value={endereco.complemento}
            onChange={onChange}
            disabled={disabled}
            placeholder="Apartamento, bloco, etc."
          />
        </FormGroup>
      </Row>

      <Row $columns="1fr">
        <FormGroup>
          <Label>
            Bairro <span>*</span>
          </Label>
          <Input
            type="text"
            name="bairro"
            value={endereco.bairro}
            onChange={onChange}
            disabled={disabled}
            placeholder="Nome do bairro"
            $error={errors.bairro}
          />
          {errors.bairro && <ErrorText>{errors.bairro}</ErrorText>}
        </FormGroup>
      </Row>

      <Row $columns="2fr 1fr">
        <FormGroup>
          <Label>
            Cidade <span>*</span>
          </Label>
          <Input
            type="text"
            name="cidade"
            value={endereco.cidade}
            onChange={onChange}
            disabled={disabled}
            placeholder="Nome da cidade"
            $error={errors.cidade}
          />
          {errors.cidade && <ErrorText>{errors.cidade}</ErrorText>}
        </FormGroup>

        <FormGroup>
          <Label>
            UF <span>*</span>
          </Label>
          <Input
            type="text"
            name="uf"
            value={endereco.uf}
            onChange={onChange}
            disabled={disabled}
            placeholder="SP"
            maxLength={2}
            style={{ textTransform: 'uppercase' }}
            $error={errors.uf}
          />
          {errors.uf && <ErrorText>{errors.uf}</ErrorText>}
        </FormGroup>
      </Row>
    </Section>
  );
};
