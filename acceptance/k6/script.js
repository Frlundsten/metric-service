import http from 'k6/http';
import {sleep} from 'k6';

export const options = {
    stages: [
        {duration: '3s', target: 10},
        {duration: '4s', target: 50},
        {duration: '3s', target: 0},
    ],
    thresholds: {
        http_req_duration: ['p(95)<200'],
    }
};

export default function () {
    const url = 'http://localhost:8080/metrics/recent';

    const headers = {
        'Repository-Id': 'repo-1'
    };

    const res = http.get(url, {headers});

    console.log(`Request status: ${res.status}`);

    sleep(1);
}

export function handleSummary(data) {
    http.post('http://localhost:8080/metrics', JSON.stringify(data), {
        headers: {
            'Content-Type': 'application/json',
            'Repository-Id': 'fredd'
        }
    });
    return {
        // 'summary.json': JSON.stringify(data),
    };
}