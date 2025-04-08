import React, { useState } from 'react';
import { Routes, Route, useNavigate } from 'react-router-dom';
import Landing from './components/Landing';
import Register from './components/Register';
import Login from './components/Login';
import History from './components/History';
import './App.css';

function App() {
    const [token, setToken] = useState(localStorage.getItem('token'));
    const [username, setUsername] = useState(localStorage.getItem('username'));
    const navigate = useNavigate();

    const handleLogin = (token, username) => {
        setToken(token);
        setUsername(username);
        localStorage.setItem('token', token);
        localStorage.setItem('username', username);
        navigate('/history');
    };

    const handleLogout = () => {
        setToken(null);
        setUsername(null);
        localStorage.removeItem('token');
        localStorage.removeItem('username');
        navigate('/login');
    };

    const handleRegister = () => {
        navigate('/login');
    };

    return (
        <div className="App">
            <Routes>
                <Route path="/" element={<Landing />} />
                <Route path="/register" element={<Register onRegister={handleRegister} />} />
                <Route path="/login" element={<Login onLogin={handleLogin} />} />
                <Route
                    path="/history"
                    element={
                        token ? (
                            <History username={username} token={token} onLogout={handleLogout} />
                        ) : (
                            <Login onLogin={handleLogin} />
                        )
                    }
                />
            </Routes>
        </div>
    );
}

export default App;