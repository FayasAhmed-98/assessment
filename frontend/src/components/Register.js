import React, { useState } from 'react';
import { registerUser } from '../api';
import {useNavigate} from "react-router-dom";

function Register({ onRegister }) {
    const [formData, setFormData] = useState({
        username: '',
        email: '',
        password: '',
    });
    const [message, setMessage] = useState('');
    const navigate = useNavigate();
    const handleChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            await registerUser(formData);
            setMessage('Registration successful! Redirecting to login...');
            setTimeout(() => onRegister(), 2000); // Redirect after 2 seconds
        } catch (error) {
            setMessage('Registration failed.');
        }
    };
    const handleLoginRedirect = () => {
        navigate('/login'); // Navigate to login page
    };
    return (
        <div className="form-container">
            <h2>Register</h2>
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
                    type="email"
                    name="email"
                    placeholder="Email"
                    value={formData.email}
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
                <button type="submit">Register</button>
            </form>
            <p className="register-prompt">
                Already have an account?{' '}
                <button className="register-btn" onClick={handleLoginRedirect}>
                    Login
                </button>
            </p>
            {message && <p className="message">{message}</p>}
        </div>
    );
}

export default Register;