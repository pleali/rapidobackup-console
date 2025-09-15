import React from 'react'
import ReactDOM from 'react-dom/client'
import App from './App.tsx'
import './styles/index.css'
import '@/config/i18n'; // Initialize i18next

ReactDOM.createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    {/* I18nextProvider is not needed here as initReactI18next handles it */}
    <App />
  </React.StrictMode>
)
