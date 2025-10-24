const getApiUrl = () => {
    if (typeof window === 'undefined') {
        return process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080';
    }

    return `${window.location.protocol}//${window.location.hostname}:8080`;
};

export default getApiUrl;