let endpoints = {
    'heroes': 'http://localhost:8080/api/heroes/random',
    'villains': 'http://localhost:8081/api/villains/random',
    'fight': 'http://localhost:8082/api/fights'
};

try {
    const response = await fetch("/config/endpoints");
    if (response.ok) {
        endpoints = await response.json();
    } else {
        throw new Error(`The endpoint failed with status code ${response.status}`)
    }
} catch (err) { 
    console.error(`Configuration endpoint failure, assuming local development: ${err}`)
}

export default {
    endpoints
}

