import React from 'react';
import { FaTimes } from 'react-icons/fa';
import { useClienteForm } from '../../hooks/useClienteForm';
import {
  Overlay,
  Modal,
  Header,
  Title,
  CloseButton,
  Content,
  Form,
  Footer,
  Button,
} from './styles';
import { DadosPessoaisSection } from './components/DadosPessoaisSection';
import { EnderecoSection } from './components/EnderecoSection';
import { TelefonesSection, EmailsSection } from './components/ContactSections';

/**
 * Modal para cadastro/edição de clientes
 * 
 * @param {Object} cliente - Cliente para edição (opcional)
 * @param {boolean} viewMode - Modo visualização (desabilita edição)
 * @param {Function} onClose - Callback para fechar modal
 * @param {Function} onSave - Callback após salvar com sucesso
 */
function ClienteModal({ cliente, viewMode = false, onClose, onSave }) {
  const {
    formData,
    errors,
    loading,
    handleChange,
    handleEnderecoChange,
    handleSubmit,
    updateEndereco,
    telefones,
    emails,
  } = useClienteForm(cliente, onSave, onClose);

  const handleOverlayClick = (e) => {
    if (e.target === e.currentTarget) {
      onClose();
    }
  };

  const title = viewMode 
    ? 'Visualizar Cliente' 
    : cliente?.id 
      ? 'Editar Cliente' 
      : 'Novo Cliente';

  return (
    <Overlay onClick={handleOverlayClick}>
      <Modal>
        <Header>
          <Title>{title}</Title>
          <CloseButton onClick={onClose} type="button">
            <FaTimes />
          </CloseButton>
        </Header>

        <Content>
          <Form onSubmit={handleSubmit}>
            {/* Dados Pessoais */}
            <DadosPessoaisSection
              formData={formData}
              errors={errors}
              onChange={handleChange}
              disabled={viewMode}
            />

            {/* Endereço */}
            <EnderecoSection
              endereco={formData.endereco}
              errors={errors}
              onChange={handleEnderecoChange}
              onCepComplete={updateEndereco}
              disabled={viewMode}
            />

            {/* Telefones */}
            <TelefonesSection
              telefones={formData.telefones}
              errors={errors}
              telefonesHandlers={telefones}
              disabled={viewMode}
            />

            {/* E-mails */}
            <EmailsSection
              emails={formData.emails}
              errors={errors}
              emailsHandlers={emails}
              disabled={viewMode}
            />
          </Form>
        </Content>

        <Footer>
          <Button type="button" onClick={onClose}>
            {viewMode ? 'Fechar' : 'Cancelar'}
          </Button>
          {!viewMode && (
            <Button 
              type="submit" 
              $variant="primary" 
              disabled={loading}
              onClick={handleSubmit}
            >
              {loading ? 'Salvando...' : 'Salvar'}
            </Button>
          )}
        </Footer>
      </Modal>
    </Overlay>
  );
}

export default ClienteModal;
