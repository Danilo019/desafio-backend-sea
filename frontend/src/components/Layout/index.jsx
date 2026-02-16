import React from 'react';
import styled from 'styled-components';
import { useNavigate, useLocation } from 'react-router-dom';
import { FaHome, FaUsers, FaSignOutAlt, FaUser, FaChartBar } from 'react-icons/fa';
import { authService } from '../../services/authService';
import { colors, borderRadius, spacing, shadows } from '../../styles/theme';
import { toast } from 'react-toastify';

const LayoutContainer = styled.div`
  min-height: 100vh;
  display: flex;
  background: linear-gradient(135deg, ${colors.primaryDark} 0%, ${colors.primary} 100%);
`;

const Sidebar = styled.aside`
  width: 280px;
  background: ${colors.cardBg};
  box-shadow: ${shadows.lg};
  display: flex;
  flex-direction: column;
  position: fixed;
  height: 100vh;
  left: 0;
  top: 0;
  z-index: 100;
`;

const LogoSection = styled.div`
  padding: ${spacing.xl};
  border-bottom: 1px solid ${colors.border};
  display: flex;
  align-items: center;
  gap: ${spacing.md};
`;

const Logo = styled.div`
  width: 50px;
  height: 50px;
  background: linear-gradient(135deg, ${colors.accent} 0%, ${colors.accentLight} 100%);
  border-radius: ${borderRadius.md};
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  box-shadow: 0 4px 12px rgba(255, 152, 0, 0.3);
`;

const LogoText = styled.div`
  h2 {
    font-size: 18px;
    margin-bottom: 2px;
    color: ${colors.textPrimary};
  }
  
  p {
    font-size: 12px;
    color: ${colors.textSecondary};
  }
`;

const Nav = styled.nav`
  flex: 1;
  padding: ${spacing.lg};
  overflow-y: auto;
`;

const NavItem = styled.button`
  width: 100%;
  display: flex;
  align-items: center;
  gap: ${spacing.md};
  padding: ${spacing.md} ${spacing.lg};
  background: ${props => props.$active ? colors.primaryLight : 'transparent'};
  border: none;
  border-radius: ${borderRadius.md};
  color: ${props => props.$active ? colors.textPrimary : colors.textSecondary};
  font-size: 15px;
  font-weight: ${props => props.$active ? '600' : '400'};
  cursor: pointer;
  transition: all 0.3s ease;
  margin-bottom: ${spacing.sm};
  text-align: left;

  svg {
    font-size: 18px;
    color: ${props => props.$active ? colors.accent : colors.textSecondary};
  }

  &:hover {
    background: ${colors.primaryLight};
    color: ${colors.textPrimary};
    
    svg {
      color: ${colors.accent};
    }
  }
`;

const UserSection = styled.div`
  padding: ${spacing.lg};
  border-top: 1px solid ${colors.border};
`;

const UserInfo = styled.div`
  display: flex;
  align-items: center;
  gap: ${spacing.md};
  padding: ${spacing.md};
  background: ${colors.inputBg};
  border-radius: ${borderRadius.md};
  margin-bottom: ${spacing.md};
`;

const UserAvatar = styled.div`
  width: 40px;
  height: 40px;
  background: linear-gradient(135deg, ${colors.accent} 0%, ${colors.accentLight} 100%);
  border-radius: ${borderRadius.round};
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
`;

const UserDetails = styled.div`
  flex: 1;
  
  h4 {
    font-size: 14px;
    margin-bottom: 2px;
    color: ${colors.textPrimary};
  }
  
  p {
    font-size: 12px;
    color: ${colors.textSecondary};
  }
`;

const LogoutButton = styled.button`
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: ${spacing.sm};
  padding: ${spacing.md};
  background: ${colors.error};
  border: none;
  border-radius: ${borderRadius.md};
  color: ${colors.white};
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;

  &:hover {
    background: #d32f2f;
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(244, 67, 54, 0.3);
  }
`;

const MainContent = styled.main`
  flex: 1;
  margin-left: 280px;
  padding: ${spacing.xl};
`;

const Header = styled.header`
  background: ${colors.cardBg};
  border-radius: ${borderRadius.xl};
  padding: ${spacing.xl};
  margin-bottom: ${spacing.xl};
  box-shadow: ${shadows.md};
`;

const PageTitle = styled.h1`
  font-size: 28px;
  margin-bottom: ${spacing.sm};
  color: ${colors.textPrimary};
`;

const PageSubtitle = styled.p`
  color: ${colors.textSecondary};
  font-size: 14px;
`;

const Content = styled.div`
  animation: fadeIn 0.5s ease-in-out;
`;

function Layout({ children, title, subtitle }) {
  const navigate = useNavigate();
  const location = useLocation();
  const user = authService.getCurrentUser();
  const isAdmin = authService.isAdmin();

  const handleLogout = () => {
    toast.info('Saindo do sistema...');
    setTimeout(() => {
      authService.logout();
    }, 500);
  };

  const menuItems = [
    { path: '/dashboard', icon: FaHome, label: 'Dashboard' },
    { path: '/clientes', icon: FaUsers, label: 'Clientes' },
  ];

  return (
    <LayoutContainer>
      <Sidebar>
        <LogoSection>
          <Logo>🌊</Logo>
          <LogoText>
            <h2>SEA</h2>
            <p>Tecnologia</p>
          </LogoText>
        </LogoSection>

        <Nav>
          {menuItems.map((item) => (
            <NavItem
              key={item.path}
              $active={location.pathname === item.path}
              onClick={() => navigate(item.path)}
            >
              <item.icon />
              {item.label}
            </NavItem>
          ))}
        </Nav>

        <UserSection>
          <UserInfo>
            <UserAvatar>
              <FaUser />
            </UserAvatar>
            <UserDetails>
              <h4>{user?.email || 'Usuário'}</h4>
              <p>{isAdmin ? 'Administrador' : 'Usuário Padrão'}</p>
            </UserDetails>
          </UserInfo>
          
          <LogoutButton onClick={handleLogout}>
            <FaSignOutAlt />
            Sair
          </LogoutButton>
        </UserSection>
      </Sidebar>

      <MainContent>
        {(title || subtitle) && (
          <Header>
            {title && <PageTitle>{title}</PageTitle>}
            {subtitle && <PageSubtitle>{subtitle}</PageSubtitle>}
          </Header>
        )}
        <Content>{children}</Content>
      </MainContent>
    </LayoutContainer>
  );
}

export default Layout;
