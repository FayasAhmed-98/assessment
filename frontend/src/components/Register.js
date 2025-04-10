import React, { useState } from 'react';
import { registerUser } from '../api';
import { useNavigate } from "react-router-dom";

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

    const validateForm = () => {
        const { username, email, password } = formData;

        // Username validation
        if (!username.trim()) {
            setMessage("Username is required.");
            return false;
        }

        if (username.length > 8) {
            setMessage("Username must not exceed 8 characters.");
            return false;
        }

        // Email validation
        const emailRegex = /^[^\s@]+@[^\s@]+\.[a-zA-Z]{2,}$/;
        if (!emailRegex.test(email)) {
            setMessage("Please enter a valid email address.");
            return false;
        }

        // Password validation
        if (password.length < 6 || password.length > 10) {
            setMessage("Password must be 6 to 10 characters long.");
            return false;
        }

        const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d).{6,10}$/;
        if (!passwordRegex.test(password)) {
            setMessage("Password must include uppercase, lowercase, and a number.");
            return false;
        }

        return true;
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        // Frontend validation before API call
        if (!validateForm()) return;

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
