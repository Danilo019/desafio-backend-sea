import React from 'react';
import { FaPhone, FaPlus, FaEnvelope } from 'react-icons/fa';
import {
  Section,
  SectionTitle,
  ArraySection,
  AddButton,
  ErrorText,
} from '../styles';
import { TelefoneField } from './TelefoneField';
import { EmailField } from './EmailField';

/**
 * Seção de Telefones
 */
export const TelefonesSection = ({ 
  telefones, 
  errors, 
  telefonesHandlers, 
disabled 
}) => {
  return (
    <Section>
      <SectionTitle>
        <FaPhone /> Telefones
      </SectionTitle>

      {errors.telefones && <ErrorText>{errors.telefones}</ErrorText>}

      <ArraySection>
        {telefones.map((telefone, index) => (
          <TelefoneField
            key={index}
            telefone={telefone}
            index={index}
            disabled={disabled}
            onRemove={telefonesHandlers.remove}
            onChange={telefonesHandlers.handleChange}
            onPrincipalChange={telefonesHandlers.handlePrincipalChange}
            error={errors[`telefone_${index}`]}
          />
        ))}
      </ArraySection>

      {!disabled && (
        <AddButton type="button" onClick={telefonesHandlers.add}>
          <FaPlus /> Adicionar Telefone
        </AddButton>
      )}
    </Section>
  );
};

/**
 * Seção de E-mails
 */
export const EmailsSection = ({ 
  emails, 
  errors, 
  emailsHandlers, 
  disabled 
}) => {
  return (
    <Section>
      <SectionTitle>
        <FaEnvelope /> E-mails
      </SectionTitle>

      {errors.emails && <ErrorText>{errors.emails}</ErrorText>}

      <ArraySection>
        {emails.map((email, index) => (
          <EmailField
            key={index}
            email={email}
            index={index}
            disabled={disabled}
            onRemove={emailsHandlers.remove}
            onChange={emailsHandlers.handleChange}
            onPrincipalChange={emailsHandlers.handlePrincipalChange}
            error={errors[`email_${index}`]}
          />
        ))}
      </ArraySection>

      {!disabled && (
        <AddButton type="button" onClick={emailsHandlers.add}>
          <FaPlus /> Adicionar E-mail
        </AddButton>
      )}
    </Section>
  );
};
