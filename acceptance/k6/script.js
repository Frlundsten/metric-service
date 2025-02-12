import http from 'k6/http';
import { sleep } from 'k6';


export const options = {
    vus: 10,
    duration: '10s',
};

export default function () {
    http.get('http://localhost:8080/hello');
    sleep(1);
}

export function handleSummary(data) {
        console.log(JSON.stringify(data));
    return {
        // 'summary.json': JSON.stringify(data), //the default data object
        // http.post('http://localhost:8080/hello', data),
    };
}