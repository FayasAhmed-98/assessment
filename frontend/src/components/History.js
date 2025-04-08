import React, { useState, useEffect } from 'react';
import { getLoginHistory } from '../api';

function History({ username, token, onLogout }) {
    const [history, setHistory] = useState([]);
    const [error, setError] = useState('');

    useEffect(() => {
        const fetchHistory = async () => {
            try {
                const response = await getLoginHistory(username, token);
                setHistory(response.data.slice(0, 5));
            } catch (err) {
                setError('Failed to load login history.');
            }
        };
        fetchHistory();
    }, [username, token]);

    return (
        <div className="history-container">
            <h2>Login History for {username}</h2>
            {error && <p className="error">{error}</p>}
            <ul>
                {history.map((entry, index) => (
                    <li key={index}>
                        {new Date(entry.timestamp).toLocaleString()} - {entry.success ? 'Success' : 'Failure'}
                    </li>
                ))}
            </ul>
            <button className="logout-btn" onClick={onLogout}>Logout</button>
        </div>
    );
}

export default History;