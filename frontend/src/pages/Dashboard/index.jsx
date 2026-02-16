import React, { useState, useEffect } from 'react';
import styled from 'styled-components';
import Layout from '../../components/Layout';
import { clienteService } from '../../services/clienteService';
import { authService } from '../../services/authService';
import { FaUsers, FaEnvelope, FaPhone, FaMapMarkerAlt, FaUserShield, FaChartLine } from 'react-icons/fa';
import { colors, borderRadius, spacing, shadows } from '../../styles/theme';
import { useNavigate } from 'react-router-dom';

const Container = styled.div`
  animation: fadeIn 0.5s ease-in-out;
`;

const StatsGrid = styled.div`
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: ${spacing.xl};
  margin-bottom: ${spacing.xl};
`;

const StatCard = styled.div`
  background: ${colors.cardBg};
  border-radius: ${borderRadius.xl};
  padding: ${spacing.xl};
  box-shadow: ${shadows.md};
  transition: all 0.3s ease;
  cursor: pointer;
  position: relative;
  overflow: hidden;

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    width: 4px;
    height: 100%;
    background: ${props => props.$color || colors.accent};
  }

  &:hover {
    transform: translateY(-5px);
    box-shadow: ${shadows.lg};
  }
`;

const StatIcon = styled.div`
  width: 60px;
  height: 60px;
  background: ${props => props.$color || colors.accent}22;
  border-radius: ${borderRadius.md};
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  color: ${props => props.$color || colors.accent};
  margin-bottom: ${spacing.md};
`;

const StatValue = styled.div`
  font-size: 36px;
  font-weight: 700;
  color: ${colors.textPrimary};
  margin-bottom: ${spacing.sm};
`;

const StatLabel = styled.div`
  font-size: 14px;
  color: ${colors.textSecondary};
  font-weight: 500;
`;

const Section = styled.div`
  background: ${colors.cardBg};
  border-radius: ${borderRadius.xl};
  padding: ${spacing.xl};
  margin-bottom: ${spacing.xl};
  box-shadow: ${shadows.md};
`;

const SectionHeader = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: ${spacing.xl};
  padding-bottom: ${spacing.lg};
  border-bottom: 1px solid ${colors.border};
`;

const SectionTitle = styled.h2`
  font-size: 20px;
  color: ${colors.textPrimary};
  display: flex;
  align-items: center;
  gap: ${spacing.md};
  
  svg {
    color: ${colors.accent};
  }
`;

const WelcomeBanner = styled.div`
  background: linear-gradient(135deg, ${colors.primaryLight} 0%, ${colors.primary} 100%);
  border-radius: ${borderRadius.xl};
  padding: ${spacing.xxl};
  margin-bottom: ${spacing.xl};
  box-shadow: ${shadows.lg};
  position: relative;
  overflow: hidden;

  &::before {
    content: '🌊';
    position: absolute;
    right: -20px;
    bottom: -20px;
    font-size: 150px;
    opacity: 0.1;
  }
`;

const WelcomeTitle = styled.h1`
  font-size: 32px;
  margin-bottom: ${spacing.md};
  color: ${colors.textPrimary};
`;

const WelcomeText = styled.p`
  font-size: 16px;
  color: ${colors.textSecondary};
  max-width: 600px;
`;

const InfoGrid = styled.div`
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: ${spacing.lg};
`;

const InfoCard = styled.div`
  background: ${colors.inputBg};
  border-radius: ${borderRadius.md};
  padding: ${spacing.lg};
  border-left: 3px solid ${props => props.$color || colors.accent};
`;

const InfoTitle = styled.h3`
  font-size: 16px;
  color: ${colors.textPrimary};
  margin-bottom: ${spacing.md};
  display: flex;
  align-items: center;
  gap: ${spacing.sm};
  
  svg {
    color: ${props => props.$color || colors.accent};
  }
`;

const InfoList = styled.ul`
  list-style: none;
  font-size: 14px;
  color: ${colors.textSecondary};
  line-height: 1.8;
  
  li {
    margin-bottom: ${spacing.sm};
    padding-left: ${spacing.lg};
    position: relative;
    
    &::before {
      content: '•';
      position: absolute;
      left: 0;
      color: ${props => props.$color || colors.accent};
      font-weight: bold;
    }
  }
`;

const Button = styled.button`
  padding: ${spacing.md} ${spacing.xl};
  background: linear-gradient(135deg, ${colors.accent} 0%, ${colors.accentLight} 100%);
  border: none;
  border-radius: ${borderRadius.md};
  color: ${colors.white};
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(255, 152, 0, 0.3);

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 6px 16px rgba(255, 152, 0, 0.4);
  }
`;

const Loading = styled.div`
  text-align: center;
  padding: ${spacing.xxl};
  color: ${colors.textSecondary};
  
  .spinner {
    width: 50px;
    height: 50px;
    border: 4px solid ${colors.primaryLight};
    border-top-color: ${colors.accent};
    border-radius: 50%;
    animation: spin 1s linear infinite;
    margin: 0 auto ${spacing.lg};
  }
`;

function Dashboard() {
  const navigate = useNavigate();
  const user = authService.getCurrentUser();
  const isAdmin = authService.isAdmin();
  const [stats, setStats] = useState({
    totalClientes: 0,
    clientesRecentes: 0,
  });
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadStats();
  }, []);

  const loadStats = async () => {
    try {
      setLoading(true);
      const response = await clienteService.getAll(0, 100);
      setStats({
        totalClientes: response.totalElements || 0,
        clientesRecentes: response.content?.length || 0,
      });
    } catch (error) {
      console.error('Erro ao carregar estatísticas:', error);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <Layout>
        <Loading>
          <div className="spinner"></div>
          <p>Carregando dashboard...</p>
        </Loading>
      </Layout>
    );
  }

  return (
    <Layout>
      <Container>
        <WelcomeBanner>
          <WelcomeTitle>
            Bem-vindo ao Sistema de Gestão! 👋
          </WelcomeTitle>
          <WelcomeText>
            {isAdmin 
              ? 'Como administrador, você tem acesso completo ao sistema para gerenciar clientes e visualizar informações.'
              : 'Você está logado como usuário padrão. Consulte os clientes cadastrados no sistema.'}
          </WelcomeText>
        </WelcomeBanner>

        <StatsGrid>
          <StatCard $color={colors.accent} onClick={() => navigate('/clientes')}>
            <StatIcon $color={colors.accent}>
              <FaUsers />
            </StatIcon>
            <StatValue>{stats.totalClientes}</StatValue>
            <StatLabel>Total de Clientes</StatLabel>
          </StatCard>

          <StatCard $color={colors.info}>
            <StatIcon $color={colors.info}>
              <FaChartLine />
            </StatIcon>
            <StatValue>{stats.clientesRecentes}</StatValue>
            <StatLabel>Clientes Carregados</StatLabel>
          </StatCard>

          <StatCard $color={isAdmin ? colors.success : colors.warning}>
            <StatIcon $color={isAdmin ? colors.success : colors.warning}>
              <FaUserShield />
            </StatIcon>
            <StatValue>{isAdmin ? 'Admin' : 'User'}</StatValue>
            <StatLabel>Nível de Acesso</StatLabel>
          </StatCard>
        </StatsGrid>

        <Section>
          <SectionHeader>
            <SectionTitle>
              <FaUsers />
              Informações do Sistema
            </SectionTitle>
            <Button onClick={() => navigate('/clientes')}>
              Ver Clientes
            </Button>
          </SectionHeader>

          <InfoGrid>
            <InfoCard $color={colors.accent}>
              <InfoTitle $color={colors.accent}>
                <FaUsers />
                Gestão de Clientes
              </InfoTitle>
              <InfoList $color={colors.accent}>
                <li>Cadastro completo de clientes</li>
                <li>Validação de CPF e dados</li>
                <li>Múltiplos telefones e e-mails</li>
                <li>Consulta de CEP automática</li>
              </InfoList>
            </InfoCard>

            <InfoCard $color={colors.info}>
              <InfoTitle $color={colors.info}>
                <FaMapMarkerAlt />
                Endereços
              </InfoTitle>
              <InfoList $color={colors.info}>
                <li>Integração com ViaCEP</li>
                <li>Preenchimento automático</li>
                <li>Dados completos de endereço</li>
                <li>Validação de CEP</li>
              </InfoList>
            </InfoCard>

            <InfoCard $color={colors.success}>
              <InfoTitle $color={colors.success}>
                <FaPhone />
                Contatos
              </InfoTitle>
              <InfoList $color={colors.success}>
                <li>Múltiplos telefones por cliente</li>
                <li>Tipos: Celular, Residencial, Comercial</li>
                <li>Formatação automática</li>
                <li>Validação de números</li>
              </InfoList>
            </InfoCard>

            <InfoCard $color={colors.warning}>
              <InfoTitle $color={colors.warning}>
                <FaEnvelope />
                E-mails
              </InfoTitle>
              <InfoList $color={colors.warning}>
                <li>Múltiplos e-mails por cliente</li>
                <li>Validação de formato</li>
                <li>Campo obrigatório</li>
                <li>Gestão simplificada</li>
              </InfoList>
            </InfoCard>
          </InfoGrid>
        </Section>

        {isAdmin && (
          <Section>
            <SectionHeader>
              <SectionTitle>
                <FaUserShield />
                Recursos do Administrador
              </SectionTitle>
            </SectionHeader>
            
            <InfoList $color={colors.accent}>
              <li><strong>Permissão total:</strong> Criar, editar e excluir clientes</li>
              <li><strong>Gestão completa:</strong> Acesso a todas as funcionalidades do sistema</li>
              <li><strong>Controle de dados:</strong> Validação e integridade dos dados</li>
              <li><strong>Auditoria:</strong> Todas as ações são registradas no sistema</li>
            </InfoList>
          </Section>
        )}

        {!isAdmin && (
          <Section>
            <SectionHeader>
              <SectionTitle>
                <FaUserShield />
                Recursos do Usuário Padrão
              </SectionTitle>
            </SectionHeader>
            
            <InfoList $color={colors.info}>
              <li><strong>Visualização:</strong> Consultar informações dos clientes</li>
              <li><strong>Busca:</strong> Pesquisar clientes por nome, CPF ou e-mail</li>
              <li><strong>Detalhes:</strong> Ver informações completas dos cadastros</li>
              <li><strong>Limitação:</strong> Sem permissão para criar, editar ou excluir</li>
            </InfoList>
          </Section>
        )}
      </Container>
    </Layout>
  );
}

export default Dashboard;
