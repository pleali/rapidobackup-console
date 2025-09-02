import React from 'react';
import {
  Shield,
  Zap,
  Cloud,
  Users,
  CheckCircle,
  ArrowRight,
  Download,
  Star,
  Globe,
  RefreshCw,
} from 'lucide-react';
import { Alert, AlertDescription } from '@/components/ui/alert';

// Ajout de la police Inter via Google Fonts
const fontLink = document.createElement('link');
fontLink.href = 'https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700;800;900&display=swap';
fontLink.rel = 'stylesheet';
if (!document.querySelector('link[href*="Inter"]')) {
  document.head.appendChild(fontLink);
}

const RapidoBackupLanding: React.FC = () => {
  const features = [
    {
      icon: <Shield className="w-6 h-6" />,
      title: 'Sécurité Maximum',
      description: 'Chiffrement AES-256 et protection multicouche pour vos données les plus sensibles',
    },
    {
      icon: <Zap className="w-6 h-6" />,
      title: 'Sauvegarde Ultra-Rapide',
      description: 'Sauvegardez des téraoctets en quelques minutes grâce à notre technologie optimisée',
    },
    {
      icon: <Cloud className="w-6 h-6" />,
      title: 'Stockage Cloud Illimité',
      description: 'Espace de stockage extensible avec redondance géographique automatique',
    },
    {
      icon: <RefreshCw className="w-6 h-6" />,
      title: 'Synchronisation Temps Réel',
      description: 'Vos fichiers sont protégés instantanément, sans intervention manuelle',
    },
    {
      icon: <Globe className="w-6 h-6" />,
      title: 'Accès Universel',
      description: "Récupérez vos données depuis n'importe quel appareil, n'importe où",
    },
    {
      icon: <Users className="w-6 h-6" />,
      title: 'Collaboration Équipe',
      description: 'Partagez et collaborez en toute sécurité avec votre équipe',
    },
  ];

  const plans = [
    {
      name: 'Personnel',
      price: '9€',
      period: '/mois',
      features: ['100 GB de stockage', 'Sauvegarde automatique', 'Support 24/7', 'Chiffrement AES-256'],
      popular: false,
    },
    {
      name: 'Professionnel',
      price: '29€',
      period: '/mois',
      features: [
        '1 TB de stockage',
        'Sauvegarde multi-appareils',
        'Versioning avancé',
        'API complète',
        'Support prioritaire',
      ],
      popular: true,
    },
    {
      name: 'Enterprise',
      price: 'Sur mesure',
      period: '',
      features: [
        'Stockage illimité',
        'Infrastructure dédiée',
        'Conformité RGPD',
        'Intégrations sur mesure',
        'Support dédié',
      ],
      popular: false,
    },
  ];

  return (
    <div
      className="min-h-screen bg-gray-900 text-white font-inter"
      style={{ fontFamily: 'Inter, system-ui, -apple-system, sans-serif' }}
    >
      {/* Header */}
      <header className="fixed w-full top-0 z-50 bg-gray-900/85 backdrop-blur-sm border-b border-gray-800">
        <div className="container mx-auto px-6 py-4 flex items-center justify-between">
          <div className="flex items-center space-x-2">
            <div className="w-10 h-10 flex items-center justify-center">
              <img src="./logo.svg" alt="Logo" className=" text-white" />
            </div>
            <span className="text-xl font-bold">rapidobackup</span>
          </div>
          <nav className="hidden md:flex space-x-8">
            <a href="#features" className="hover:text-blue-400 transition-colors">
              Fonctionnalités
            </a>
            <a href="#pricing" className="hover:text-blue-400 transition-colors">
              Tarifs
            </a>
            <a href="#contact" className="hover:text-blue-400 transition-colors">
              Contact
            </a>
          </nav>
          <div className="flex space-x-4">
            <button
              className="px-4 py-2 text-orange-400 hover:text-orange-300 transition-colors"
              style={{ color: '#f60' }}
              onClick={() => window.location.href = '/login'}
            >
              Connexion
            </button>
            <button
              className="px-6 py-2 rounded-lg hover:opacity-90 transition-all transform hover:scale-105 text-white font-semibold"
              style={{ background: 'linear-gradient(135deg, #f60, #ff8c00)' }}
            >
              Essai Gratuit
            </button>
          </div>
        </div>
      </header>

      {/* Hero Section */}
      <section className="pt-32 pb-20 px-6">
        <div className="container mx-auto text-center">
          <div className="mb-8">
            <h1 className="text-5xl md:text-7xl font-bold mb-6">
              <span
                style={{
                  background: 'linear-gradient(135deg, #f60, #ff8c00, #ffa500)',
                  WebkitBackgroundClip: 'text',
                  WebkitTextFillColor: 'transparent',
                  backgroundClip: 'text',
                }}
              >
                Vos Données,
              </span>
              <br />
              <span className="text-white">Toujours Protégées</span>
            </h1>
            <p className="text-xl md:text-2xl text-gray-300 mb-8 max-w-3xl mx-auto">
              La solution de sauvegarde la plus rapide et sécurisée du marché. Protégez vos données critiques avec
              RapidoBackup.
            </p>
          </div>

          <div className="flex flex-col sm:flex-row gap-4 justify-center mb-12">
            <button
              className="px-8 py-4 rounded-lg text-lg font-semibold hover:opacity-90 transition-all transform hover:scale-105 flex items-center justify-center space-x-2 text-white"
              style={{ background: 'linear-gradient(135deg, #f60, #ff8c00)' }}
            >
              <Download className="w-5 h-5" />
              <span>Commencer Gratuitement</span>
            </button>
            <button className="px-8 py-4 border border-gray-600 rounded-lg text-lg font-semibold hover:bg-gray-800 transition-all flex items-center justify-center space-x-2">
              <span>Voir la Démo</span>
              <ArrowRight className="w-5 h-5" />
            </button>
          </div>

          <Alert className="max-w-md mx-auto bg-green-900/20 border-green-500/50">
            <CheckCircle className="h-4 w-4 text-green-400" />
            <AlertDescription className="text-green-500">
              <strong>30 jours d'essai gratuit</strong> - Aucune carte bancaire requise
            </AlertDescription>
          </Alert>
        </div>
      </section>

      {/* Features Section */}
      <section id="features" className="py-20 px-6 relative overflow-hidden">
        {/* Background Image */}
        <div
          className="absolute inset-0 opacity-10"
          style={{
            backgroundImage: `url("data:image/svg+xml,%3Csvg width='60' height='60' viewBox='0 0 60 60' xmlns='http://www.w3.org/2000/svg'%3E%3Cg fill='none' fill-rule='evenodd'%3E%3Cg fill='%23f60' fill-opacity='0.1'%3E%3Ccircle cx='30' cy='30' r='4'/%3E%3C/g%3E%3C/g%3E%3C/svg%3E")`,
            backgroundSize: '60px 60px',
          }}
        />

        {/* Gradient Overlay */}
        <div
          className="absolute inset-0 opacity-20"
          style={{
            background: 'radial-gradient(ellipse at center, rgba(255, 102, 0, 0.1) 0%, transparent 70%)',
          }}
        />

        <div className="container mx-auto relative z-10">
          <div className="text-center mb-16">
            <h2 className="text-4xl md:text-5xl font-bold mb-6">
              Pourquoi Choisir
              <span
                style={{
                  background: 'linear-gradient(135deg, #f60, #ff8c00)',
                  WebkitBackgroundClip: 'text',
                  WebkitTextFillColor: 'transparent',
                  backgroundClip: 'text',
                }}
              >
                {' '}
                rapidobackup
              </span>
            </h2>
            <p className="text-xl text-gray-300 max-w-2xl mx-auto">
              Une technologie de pointe au service de la protection de vos données
            </p>
          </div>

          <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-8">
            {features.map((feature, index) => (
              <div
                key={index}
                className="group p-8 bg-gray-800/80 backdrop-blur-sm rounded-2xl border border-gray-700 hover:border-orange-500/50 transition-all duration-300 hover:transform hover:scale-105"
              >
                <div
                  className="w-12 h-12 rounded-lg flex items-center justify-center mb-6 group-hover:scale-110 transition-transform"
                  style={{ background: 'linear-gradient(135deg, #f60, #ff8c00)' }}
                >
                  {feature.icon}
                </div>
                <h3 className="text-xl font-semibold mb-4">{feature.title}</h3>
                <p className="text-gray-400 leading-relaxed">{feature.description}</p>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* Stats Section */}
      <section className="py-20 px-6">
        <div className="container mx-auto">
          <div className="grid md:grid-cols-4 gap-8 text-center">
            <div className="p-6">
              <div className="text-4xl font-bold mb-2" style={{ color: '#f60' }}>
                99.9%
              </div>
              <div className="text-gray-300">Uptime Garantie</div>
            </div>
            <div className="p-6">
              <div className="text-4xl font-bold mb-2" style={{ color: '#ff8c00' }}>
                10M+
              </div>
              <div className="text-gray-300">Fichiers Sauvegardés</div>
            </div>
            <div className="p-6">
              <div className="text-4xl font-bold mb-2" style={{ color: '#ffa500' }}>
                150+
              </div>
              <div className="text-gray-300">Pays Couverts</div>
            </div>
            <div className="p-6">
              <div className="text-4xl font-bold mb-2" style={{ color: '#ff7f00' }}>
                50K+
              </div>
              <div className="text-gray-300">Clients Satisfaits</div>
            </div>
          </div>
        </div>
      </section>

      {/* Pricing Section */}
      <section id="pricing" className="py-20 px-6 bg-gray-800/50">
        <div className="container mx-auto">
          <div className="text-center mb-16">
            <h2 className="text-4xl md:text-5xl font-bold mb-6">
              Tarifs
              <span
                style={{
                  background: 'linear-gradient(135deg, #f60, #ff8c00)',
                  WebkitBackgroundClip: 'text',
                  WebkitTextFillColor: 'transparent',
                  backgroundClip: 'text',
                }}
              >
                Transparents
              </span>
            </h2>
            <p className="text-xl text-gray-300 max-w-2xl mx-auto">Choisissez le plan qui correspond à vos besoins</p>
          </div>

          <div className="grid md:grid-cols-3 gap-8 max-w-6xl mx-auto">
            {plans.map((plan, index) => (
              <div
                key={index}
                className={`relative p-8 rounded-2xl border transition-all duration-300 hover:transform hover:scale-105 ${
                  plan.popular
                    ? 'bg-gradient-to-b from-orange-500/10 to-red-500/10'
                    : 'border-gray-700 bg-gray-800 hover:border-gray-600'
                }`}
                style={plan.popular ? { borderColor: '#f60' } : {}}
              >
                {plan.popular && (
                  <div className="absolute -top-4 left-1/2 transform -translate-x-1/2">
                    <div
                      className="px-4 py-2 rounded-full text-sm font-semibold flex items-center space-x-1 text-white"
                      style={{ background: 'linear-gradient(135deg, #f60, #ff8c00)' }}
                    >
                      <Star className="w-4 h-4" />
                      <span>Populaire</span>
                    </div>
                  </div>
                )}

                <div className="text-center mb-8">
                  <h3 className="text-2xl font-bold mb-4">{plan.name}</h3>
                  <div className="mb-6">
                    <span className="text-4xl font-bold">{plan.price}</span>
                    <span className="text-gray-400">{plan.period}</span>
                  </div>
                </div>

                <ul className="space-y-4 mb-8">
                  {plan.features.map((feature, featureIndex) => (
                    <li key={featureIndex} className="flex items-center space-x-3">
                      <CheckCircle className="w-5 h-5 text-green-400 flex-shrink-0" />
                      <span className="text-gray-300">{feature}</span>
                    </li>
                  ))}
                </ul>

                <button
                  className={`w-full py-3 rounded-lg font-semibold transition-all ${
                    plan.popular
                      ? 'text-white hover:opacity-90'
                      : 'border border-gray-600 hover:bg-gray-700 text-gray-300'
                  }`}
                  style={plan.popular ? { background: 'linear-gradient(135deg, #f60, #ff8c00)' } : {}}
                >
                  {plan.name === 'Enterprise' ? 'Nous Contacter' : 'Commencer'}
                </button>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* CTA Section */}
      <section className="py-20 px-6">
        <div className="container mx-auto text-center">
          <div className="max-w-4xl mx-auto">
            <h2 className="text-4xl md:text-5xl font-bold mb-6">Prêt à Sécuriser Vos Données ?</h2>
            <p className="text-xl text-gray-300 mb-8">
              Rejoignez des milliers d'entreprises qui font confiance à RapidoBackup pour protéger leurs données
              critiques.
            </p>
            <div className="flex flex-col sm:flex-row gap-4 justify-center">
              <button
                className="px-8 py-4 rounded-lg text-lg font-semibold hover:opacity-90 transition-all transform hover:scale-105 text-white"
                style={{ background: 'linear-gradient(135deg, #f60, #ff8c00)' }}
              >
                Démarrer Maintenant
              </button>
              <button className="px-8 py-4 border border-gray-600 rounded-lg text-lg font-semibold hover:bg-gray-800 transition-all">
                Planifier une Démo
              </button>
            </div>
          </div>
        </div>
      </section>

      {/* Footer */}
      <footer className="py-12 px-6 bg-gray-800 border-t border-gray-700">
        <div className="container mx-auto">
          <div className="grid md:grid-cols-4 gap-8">
            <div>
              <div className="flex items-center space-x-2 mb-4">
                <div className="w-8 h-8 rounded-lg flex items-center justify-center">
                   <img src="./favicon.svg" alt="Logo" className="opacity-25 text-white" />
                </div>
                <span className="text-xl font-bold">rapidobackup</span>
              </div>
              <p className="text-gray-400">
                La solution de sauvegarde de confiance pour protéger vos données les plus précieuses.
              </p>
            </div>
            <div>
              <h4 className="font-semibold mb-4">Produit</h4>
              <ul className="space-y-2 text-gray-400">
                <li>
                  <a href="#" className="hover:text-white transition-colors">
                    Fonctionnalités
                  </a>
                </li>
                <li>
                  <a href="#" className="hover:text-white transition-colors">
                    Sécurité
                  </a>
                </li>
                <li>
                  <a href="#" className="hover:text-white transition-colors">
                    API
                  </a>
                </li>
                <li>
                  <a href="#" className="hover:text-white transition-colors">
                    Intégrations
                  </a>
                </li>
              </ul>
            </div>
            <div>
              <h4 className="font-semibold mb-4">Support</h4>
              <ul className="space-y-2 text-gray-400">
                <li>
                  <a href="#" className="hover:text-white transition-colors">
                    Centre d'aide
                  </a>
                </li>
                <li>
                  <a href="#" className="hover:text-white transition-colors">
                    Documentation
                  </a>
                </li>
                <li>
                  <a href="#" className="hover:text-white transition-colors">
                    Contact
                  </a>
                </li>
                <li>
                  <a href="#" className="hover:text-white transition-colors">
                    Status
                  </a>
                </li>
              </ul>
            </div>
            <div>
              <h4 className="font-semibold mb-4">Entreprise</h4>
              <ul className="space-y-2 text-gray-400">
                <li>
                  <a href="#" className="hover:text-white transition-colors">
                    À propos
                  </a>
                </li>
                <li>
                  <a href="#" className="hover:text-white transition-colors">
                    Carrières
                  </a>
                </li>
                <li>
                  <a href="#" className="hover:text-white transition-colors">
                    Presse
                  </a>
                </li>
                <li>
                  <a href="#" className="hover:text-white transition-colors">
                    Partenaires
                  </a>
                </li>
              </ul>
            </div>
          </div>
          <div className="border-t border-gray-700 mt-8 pt-8 flex flex-col md:flex-row justify-between items-center">
            <p className="text-gray-400">© 2025 RapidoBackup. Tous droits réservés.</p>
            <div className="flex space-x-6 mt-4 md:mt-0">
              <a href="#" className="text-gray-400 hover:text-white transition-colors">
                Confidentialité
              </a>
              <a href="#" className="text-gray-400 hover:text-white transition-colors">
                CGU
              </a>
              <a href="#" className="text-gray-400 hover:text-white transition-colors">
                RGPD
              </a>
            </div>
          </div>
        </div>
      </footer>
    </div>
  );
};

export default RapidoBackupLanding;
