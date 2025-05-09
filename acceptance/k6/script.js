import http from 'k6/http';
import { sleep } from 'k6';

export const options = {
    stages: [
        { duration: '3s', target: 10 },
        { duration: '4s', target: 50 },
        { duration: '3s', target: 0 },
    ],
    thresholds: {
        http_req_duration: ['p(95)<200'], // 95% of requests should complete below 200ms
        http_req_failed: ['rate<0.05'],   // Failures should be less than 5%
    }
};

export default function () {
    const url = 'http://localhost:8080/metrics/recent';

    const headers = {
        'Repository-Id': 'repo-1'
    };

    const res = http.get(url, { headers });

    console.log(`Request status: ${res.status}`);

    sleep(1);
}

export function handleSummary(data) {
//         console.log(JSON.stringify(data));
    return {
        'summary.json': JSON.stringify(data), //the default data object
//         // http.post('http://localhost:8080/hello', data),
    };
}