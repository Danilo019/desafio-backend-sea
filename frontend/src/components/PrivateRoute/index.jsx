import React from 'react';
import { Navigate } from 'react-router-dom';
import { authService } from '../../services/authService';

function PrivateRoute({ children }) {
  const isAuthenticated = authService.isAuthenticated();
  
  console.log('🛡️ PrivateRoute - Verificando autenticação...');
  console.log('  - Token existe:', !!localStorage.getItem('token'));
  console.log('  - User existe:', !!localStorage.getItem('user'));
  console.log('  - isAuthenticated:', isAuthenticated);
  
  if (!isAuthenticated) {
    console.log('❌ Não autenticado, redirecionando para /login');
    return <Navigate to="/login" replace />;
  }
  
  console.log('✅ Autenticado, renderizando componente protegido');
  return children;
}

export default PrivateRoute;
