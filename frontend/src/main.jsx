import React from 'react';
import ReactDOM from 'react-dom/client';
import App from './App.jsx';
import { AdminApp } from './admin/AdminApp.jsx';
import './styles.css';
import './i18n';

// The unlisted admin panel is the only other page, so a path check beats pulling in a router.
const isAdminRoute = window.location.pathname.replace(/\/+$/, '') === '/admin';

ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    {isAdminRoute ? <AdminApp /> : <App />}
  </React.StrictMode>
);
