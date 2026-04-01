import SockJS from "sockjs-client";
import {Client, type IMessage} from "@stomp/stompjs";
import {useAuth} from "@/store/auth";

const API_BASE = (import.meta.env.VITE_API_BASE as string) || "http://localhost:8081";
const WS_PATH = (import.meta.env.VITE_WS_PATH as string) || "/ws";

export function createStompClient(onConnect?: () => void) {
    const { token } = useAuth();
    const sock = new SockJS(`${API_BASE}${WS_PATH}`);

    const client = new Client({
        webSocketFactory: () => sock as any,
        // Pass JWT in the connect frame
        connectHeaders: {
            Authorization: `Bearer ${token.value}`
        },
        reconnectDelay: 5000,
        heartbeatIncoming: 4000,
        heartbeatOutgoing: 4000
    });

    client.onConnect = () => onConnect?.();
    client.activate();
    return client;
}

export function subscribeTopic(client: Client, topic: string, cb: (data: any) => void) {
    const sub = client.subscribe(topic, (msg: IMessage) => {
        if (msg && msg.body) cb(JSON.parse(msg.body));
    });
    return () => sub.unsubscribe();
}