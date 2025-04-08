import axios from 'axios';

const API_URL = 'http://localhost:8080/api/auth';

export const registerUser = async (userData) => {
    return axios.post(`${API_URL}/register`, userData);
};

export const loginUser = async (credentials) => {
    return axios.post(`${API_URL}/login`, credentials);
};

export const getLoginHistory = async (username, token) => {
    return axios.get(`${API_URL}/history/${username}`, {
        headers: { Authorization: `Bearer ${token}` },
    });
};