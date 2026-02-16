import api from './api';

export const authService = {
  async login(email, senha) {
    try {
      console.log('🔐 Iniciando login para:', email);
      const response = await api.post('/auth/login', { email, senha });
      console.log('📨 Resposta do login:', response.data);
      const { token } = response.data;
      
      if (token) {
        console.log('✅ Token recebido, salvando...');
        localStorage.setItem('token', token);
        
        // Buscar informações do usuário através do endpoint /auth/me
        try {
          console.log('👤 Buscando dados do usuário...');
          const userResponse = await api.get('/auth/me');
          console.log('📋 Dados do usuário recebidos:', userResponse.data);
          const user = {
            email: userResponse.data.email,
            role: userResponse.data.role || 'ROLE_USER',
          };
          localStorage.setItem('user', JSON.stringify(user));
          console.log('✅ Usuário salvo no localStorage:', user);
          return { success: true, user };
        } catch (meError) {
          console.warn('⚠️ Falha ao buscar /auth/me, usando fallback:', meError.message);
          // Se falhar ao buscar /me, usar o email do login
          const user = {
            email: email,
            role: email.includes('admin') ? 'ROLE_ADMIN' : 'ROLE_USER',
          };
          localStorage.setItem('user', JSON.stringify(user));
          console.log('✅ Usuário salvo (fallback):', user);
          return { success: true, user };
        }
      }
      
      console.error('❌ Token não recebido na resposta');
      return { success: false, message: 'Token não recebido' };
    } catch (error) {
      console.error('❌ Erro no login:', error);
      console.error('❌ Detalhes do erro:', error.response?.data);
      return {
        success: false,
        message: error.response?.data?.message || 'Erro ao fazer login',
      };
    }
  },

  logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    window.location.href = '/login';
  },

  getCurrentUser() {
    const userStr = localStorage.getItem('user');
    if (userStr) {
      return JSON.parse(userStr);
    }
    return null;
  },

  isAuthenticated() {
    const token = localStorage.getItem('token');
    const user = localStorage.getItem('user');
    // Simplesmente verifica se token e usuário existem no localStorage
    return !!(token && user);
  },

  isAdmin() {
    const user = this.getCurrentUser();
    return user?.role === 'ROLE_ADMIN';
  },
};
