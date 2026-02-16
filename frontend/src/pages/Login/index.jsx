import React, { useState } from 'react';
import styled from 'styled-components';
import { useNavigate } from 'react-router-dom';
import { toast } from 'react-toastify';
import { FaEnvelope, FaLock, FaUser } from 'react-icons/fa';
import { authService } from '../../services/authService';
import { colors, borderRadius, spacing } from '../../styles/theme';

const LoginContainer = styled.div`
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: ${spacing.lg};
  background: linear-gradient(135deg, ${colors.primaryDark} 0%, ${colors.primary} 100%);
`;

const LoginCard = styled.div`
  background: ${colors.cardBg};
  border-radius: ${borderRadius.xl};
  padding: ${spacing.xxl};
  width: 100%;
  max-width: 450px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  animation: fadeIn 0.5s ease-in-out;
`;

const LogoSection = styled.div`
  text-align: center;
  margin-bottom: ${spacing.xl};
`;

const Logo = styled.div`
  width: 80px;
  height: 80px;
  background: linear-gradient(135deg, ${colors.accent} 0%, ${colors.accentLight} 100%);
  border-radius: ${borderRadius.round};
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto ${spacing.md};
  font-size: 36px;
  box-shadow: 0 10px 30px rgba(255, 152, 0, 0.3);
`;

const Title = styled.h1`
  font-size: 28px;
  margin-bottom: ${spacing.sm};
  color: ${colors.textPrimary};
`;

const Subtitle = styled.p`
  color: ${colors.textSecondary};
  font-size: 14px;
`;

const Form = styled.form`
  display: flex;
  flex-direction: column;
  gap: ${spacing.lg};
`;

const InputGroup = styled.div`
  position: relative;
`;

const InputIcon = styled.div`
  position: absolute;
  left: ${spacing.md};
  top: 50%;
  transform: translateY(-50%);
  color: ${colors.textSecondary};
  font-size: 18px;
`;

const Input = styled.input`
  width: 100%;
  padding: ${spacing.md} ${spacing.md} ${spacing.md} 48px;
  background: ${colors.inputBg};
  border: 2px solid ${colors.inputBorder};
  border-radius: ${borderRadius.md};
  color: ${colors.textPrimary};
  font-size: 16px;
  transition: all 0.3s ease;

  &:focus {
    outline: none;
    border-color: ${colors.accent};
    box-shadow: 0 0 0 3px rgba(255, 152, 0, 0.1);
  }

  &::placeholder {
    color: ${colors.textMuted};
  }
`;

const Button = styled.button`
  padding: ${spacing.md} ${spacing.lg};
  background: linear-gradient(135deg, ${colors.accent} 0%, ${colors.accentLight} 100%);
  border: none;
  border-radius: ${borderRadius.md};
  color: ${colors.white};
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(255, 152, 0, 0.3);

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 6px 20px rgba(255, 152, 0, 0.4);
  }

  &:active {
    transform: translateY(0);
  }

  &:disabled {
    opacity: 0.6;
    cursor: not-allowed;
    transform: none;
  }
`;

const InfoBox = styled.div`
  background: ${colors.primaryLight};
  border-radius: ${borderRadius.md};
  padding: ${spacing.lg};
  margin-top: ${spacing.lg};
`;

const InfoTitle = styled.h3`
  font-size: 14px;
  color: ${colors.textPrimary};
  margin-bottom: ${spacing.md};
  display: flex;
  align-items: center;
  gap: ${spacing.sm};
`;

const InfoText = styled.p`
  font-size: 13px;
  color: ${colors.textSecondary};
  margin: ${spacing.sm} 0;
  line-height: 1.5;
`;

const UserBadge = styled.div`
  background: ${colors.inputBg};
  border-radius: ${borderRadius.md};
  padding: ${spacing.md};
  margin-top: ${spacing.sm};
`;

const UserInfo = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: ${spacing.sm};
  
  strong {
    color: ${colors.accent};
    font-size: 14px;
  }
  
  span {
    font-size: 13px;
    color: ${colors.textSecondary};
  }
`;

function Login() {
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    email: '',
    senha: '',
  });
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!formData.email || !formData.senha) {
      toast.error('Preencha todos os campos');
      return;
    }

    console.log('📝 Iniciando submit do formulário...');
    setLoading(true);
    
    try {
      console.log('🔄 Chamando authService.login...');
      const result = await authService.login(formData.email, formData.senha);
      console.log('🎯 Resultado do login:', result);
      
      if (result.success) {
        console.log('✅ Login bem-sucedido, verificando localStorage...');
        console.log('Token no localStorage:', !!localStorage.getItem('token'));
        console.log('User no localStorage:', !!localStorage.getItem('user'));
        
        toast.success('Login realizado com sucesso!');
        console.log('🚀 Navegando para dashboard...');
        navigate('/dashboard');
      } else {
        console.error('❌ Login falhou:', result.message);
        toast.error(result.message || 'Erro ao fazer login');
      }
    } catch (error) {
      console.error('💥 Exceção no handleSubmit:', error);
      toast.error('Erro ao fazer login. Verifique suas credenciais.');
    } finally {
      setLoading(false);
      console.log('✅ Loading finalizado');
    }
  };

  const fillAdminCredentials = () => {
    setFormData({
      email: 'admin@sea.com',
      senha: '123qwe!@#',
    });
  };

  const fillUserCredentials = () => {
    setFormData({
      email: 'user@sea.com',
      senha: '123qwe123',
    });
  };

  return (
    <LoginContainer>
      <LoginCard>
        <LogoSection>
          <Logo>🌊</Logo>
          <Title>Desafio Backend</Title>
          <Subtitle>Sistema de Gestão de Clientes - SEA Tecnologia</Subtitle>
        </LogoSection>

        <Form onSubmit={handleSubmit}>
          <InputGroup>
            <InputIcon>
              <FaEnvelope />
            </InputIcon>
            <Input
              type="email"
              name="email"
              placeholder="E-mail"
              value={formData.email}
              onChange={handleChange}
              autoComplete="email"
            />
          </InputGroup>

          <InputGroup>
            <InputIcon>
              <FaLock />
            </InputIcon>
            <Input
              type="password"
              name="senha"
              placeholder="Senha"
              value={formData.senha}
              onChange={handleChange}
              autoComplete="current-password"
            />
          </InputGroup>

          <Button type="submit" disabled={loading}>
            {loading ? 'Entrando...' : 'Entrar'}
          </Button>
        </Form>

        <InfoBox>
          <InfoTitle>
            <FaUser /> Credenciais de Teste
          </InfoTitle>
          
          <UserBadge>
            <UserInfo>
              <strong>1- Usuário Admin</strong>
              <span onClick={fillAdminCredentials} style={{ cursor: 'pointer', color: colors.accent }}>
                Preencher
              </span>
            </UserInfo>
            <InfoText><strong>E-mail:</strong> admin@sea.com</InfoText>
            <InfoText><strong>Senha:</strong> 123qwe!@#</InfoText>
            <InfoText style={{ fontSize: '12px', marginTop: spacing.sm, color: colors.textMuted }}>
              <em>Permissão total no sistema</em>
            </InfoText>
          </UserBadge>

          <UserBadge>
            <UserInfo>
              <strong>2- Usuário Padrão</strong>
              <span onClick={fillUserCredentials} style={{ cursor: 'pointer', color: colors.accent }}>
                Preencher
              </span>
            </UserInfo>
            <InfoText><strong>E-mail:</strong> user@sea.com</InfoText>
            <InfoText><strong>Senha:</strong> 123qwe123</InfoText>
            <InfoText style={{ fontSize: '12px', marginTop: spacing.sm, color: colors.textMuted }}>
              <em>Permissão de visualização</em>
            </InfoText>
          </UserBadge>
        </InfoBox>
      </LoginCard>
    </LoginContainer>
  );
}

export default Login;
