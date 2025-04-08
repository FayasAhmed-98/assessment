import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom'; // Import useNavigate
import { loginUser } from '../api';

function Login({ onLogin }) {
    const [formData, setFormData] = useState({ username: '', password: '' });
    const [message, setMessage] = useState('');
    const navigate = useNavigate();

    const handleChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            const response = await loginUser(formData);
            setMessage('Login successful! Redirecting...');
            setTimeout(() => onLogin(response.data, formData.username), 1000); // Redirect after 1 second
        } catch (error) {
            setMessage('Login failed. Please check your credentials.');
        }
    };

    const handleRegisterRedirect = () => {
        navigate('/register'); // Navigate to register page
    };

    return (
        <div className="form-container">
            <h2>Login</h2>
            <form onSubmit={handleSubmit}>
                <input
                    type="text"
                    name="username"
                    placeholder="Username"
                    value={formData.username}
                    onChange={handleChange}
                    required
                />
                <input
                    type="password"
                    name="password"
                    placeholder="Password"
                    value={formData.password}
                    onChange={handleChange}
                    required
                />
                <button type="submit">Login</button>
            </form>
            <p className="register-prompt">
                Don't have an account?{' '}
                <button className="register-btn" onClick={handleRegisterRedirect}>
                    Register
                </button>
            </p>
            {message && <p className="message">{message}</p>}
        </div>
    );
}

export default Login;