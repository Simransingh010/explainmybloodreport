import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import ReactMarkdown from 'react-markdown';
import MetricsChart from '../components/MetricsChart';
import './AnalyzerPage.css';

function AnalyzerPage() {
  const navigate = useNavigate();
  const [file, setFile] = useState(null);
  const [loading, setLoading] = useState(false);
  const [analysis, setAnalysis] = useState(null);
  const [error, setError] = useState('');
  const [uploadProgress, setUploadProgress] = useState(0);

  const handleFileChange = (e) => {
    const selectedFile = e.target.files[0];
    setFile(selectedFile);
    setError('');
    setAnalysis(null);
  };

  const handleUpload = async () => {
    if (!file) {
      setError('Please select a file first!');
      return;
    }

    setLoading(true);
    setError('');
    setUploadProgress(0);

    // Simulate upload progress
    const progressInterval = setInterval(() => {
      setUploadProgress(prev => {
        if (prev >= 90) {
          clearInterval(progressInterval);
          return 90;
        }
        return prev + 10;
      });
    }, 200);

    const formData = new FormData();
    formData.append('file', file);

    try {
      const apiUrl = import.meta.env.VITE_API_URL || 'http://localhost:8080';
      const response = await fetch(`${apiUrl}/api/blood-report/upload`, {
        method: 'POST',
        body: formData,
      });

      const data = await response.json();
      clearInterval(progressInterval);
      setUploadProgress(100);

      if (response.ok) {
        setTimeout(() => setAnalysis(data), 300);
      } else {
        // Handle specific error codes
        if (response.status === 409) {
          setError(data.message || 'An upload is already in progress. Please wait.');
        } else if (response.status === 429) {
          setError(data.message || 'Rate limit exceeded. Please try again later.');
        } else {
          setError(data.error || 'Failed to analyze the report');
        }
      }
    } catch (err) {
      clearInterval(progressInterval);
      setError('Failed to connect to the server. Make sure the backend is running!');
    } finally {
      setLoading(false);
      setTimeout(() => setUploadProgress(0), 1000);
    }
  };

  return (
    <div className="analyzer-page">
      <nav className="navbar">
        <div className="container">
          <h2 className="logo" onClick={() => navigate('/')} style={{ cursor: 'pointer' }}>
            🩺 BloodReport.ai
          </h2>
        </div>
      </nav>

      <main className="main-content">
        <div className="container">
          {!analysis ? (
            <div className="upload-section">
              <div className="hero-text">
                <h1 className="page-title">Get Your Blood Report Explained</h1>
                <p className="page-subtitle">
                  Upload your blood test results and get AI-powered insights in simple, 
                  easy-to-understand language. Like explaining to a 10-year-old! 🎯
                </p>
              </div>

              <div className="upload-card">
                <div className="file-input-wrapper">
                  <input
                    type="file"
                    id="file-input"
                    accept=".pdf,.jpg,.jpeg,.png"
                    onChange={handleFileChange}
                    className="file-input"
                  />
                  <label htmlFor="file-input" className="file-label">
                    <div className="upload-icon-wrapper">
                      <span className="upload-icon">📄</span>
                    </div>
                    <span className="upload-text">
                      {file ? file.name : 'Drop your blood report here'}
                    </span>
                    <span className="upload-hint">or click to browse • PDF, JPG, PNG (Max 10MB)</span>
                  </label>
                </div>

                {uploadProgress > 0 && uploadProgress < 100 && (
                  <div className="progress-bar-container">
                    <div className="progress-bar" style={{ width: `${uploadProgress}%` }}></div>
                  </div>
                )}

                <button
                  className="analyze-button"
                  onClick={handleUpload}
                  disabled={!file || loading}
                >
                  {loading ? (
                    <>
                      <span className="spinner"></span>
                      Analyzing... 
                    </>
                  ) : (
                    <>
                      Analyze Report
                      <span className="button-arrow">→</span>
                    </>
                  )}
                </button>

                {error && (
                  <div className="error-message">
                    <span className="error-icon">⚠️</span>
                    {error}
                  </div>
                )}

                <div className="trust-indicators">
                  <div className="trust-item">
                    <span className="trust-icon">🔒</span>
                    <span>Secure & Private</span>
                  </div>
                  <div className="trust-item">
                    <span className="trust-icon">⚡</span>
                    <span>Instant Results</span>
                  </div>
                  <div className="trust-item">
                    <span className="trust-icon">🤖</span>
                    <span>AI-Powered</span>
                  </div>
                </div>
              </div>
            </div>
          ) : (
            <div className="results-section">
              <div className="results-header">
                <div className="results-title-section">
                  <h1 className="results-title">Your Health Insights</h1>
                  <p className="results-subtitle">
                    Here's what we found in your blood report, explained simply
                  </p>
                </div>
                <button className="new-report-btn" onClick={() => {
                  setFile(null);
                  setAnalysis(null);
                  setError('');
                }}>
                  <span>Upload New Report</span>
                  <span className="btn-icon">↻</span>
                </button>
              </div>

              {/* Validation Warnings */}
              {analysis.validationWarnings && analysis.validationWarnings.length > 0 && (
                <div className="validation-warnings-section">
                  <div className="warning-header">
                    <span className="warning-icon">🔍</span>
                    <h3>Validation Results</h3>
                  </div>
                  <div className="warnings-list">
                    {analysis.validationWarnings.map((warning, index) => (
                      <div key={index} className="warning-item">
                        {warning}
                      </div>
                    ))}
                  </div>
                </div>
              )}

              {/* Metrics Visualization */}
              {analysis.metrics && analysis.metrics.length > 0 && (
                <MetricsChart metrics={analysis.metrics} />
              )}

              <div className="results-grid">
                {/* Risk Factors Card */}
                <div className="result-card risk-card">
                  <div className="card-header">
                    <div className="card-icon risk-icon">⚠️</div>
                    <div className="card-title-section">
                      <h2 className="card-title">Things to Watch</h2>
                      <p className="card-subtitle">Areas that need your attention</p>
                    </div>
                  </div>
                  <div className="card-content">
                    {analysis.riskFactors.map((risk, index) => (
                      <div key={index} className="insight-item risk-item">
                        <span className="item-number">{index + 1}</span>
                        <div className="item-text markdown-content">
                          <ReactMarkdown>{risk}</ReactMarkdown>
                        </div>
                      </div>
                    ))}
                  </div>
                </div>

                {/* Lifestyle Advice Card */}
                <div className="result-card advice-card">
                  <div className="card-header">
                    <div className="card-icon advice-icon">💪</div>
                    <div className="card-title-section">
                      <h2 className="card-title">Your Action Plan</h2>
                      <p className="card-subtitle">Simple steps to improve your health</p>
                    </div>
                  </div>
                  <div className="card-content">
                    {analysis.lifestyleAdvice.map((advice, index) => (
                      <div key={index} className="insight-item advice-item">
                        <span className="item-check">✓</span>
                        <div className="item-text markdown-content">
                          <ReactMarkdown>{advice}</ReactMarkdown>
                        </div>
                      </div>
                    ))}
                  </div>
                </div>
              </div>

              <div className="results-footer">
                <div className="disclaimer">
                  <span className="disclaimer-icon">ℹ️</span>
                  <p>
                    This is AI-generated educational content. Always consult with your healthcare 
                    provider for medical advice and interpretation of your blood test results.
                  </p>
                </div>
              </div>
            </div>
          )}
        </div>
      </main>
    </div>
  );
}

export default AnalyzerPage;
