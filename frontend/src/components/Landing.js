import React from 'react';
import { useNavigate } from 'react-router-dom';

function Landing() {
    const navigate = useNavigate();

    return (
        <div className="landing">
            <h1>Welcome to User Auth App</h1>
            <p>Please choose an option to proceed:</p>
            <div className="button-group">
                <button onClick={() => navigate('/login')}>Login</button>
                <button onClick={() => navigate('/register')}>Register</button>
            </div>
        </div>
    );
}

export default Landing;