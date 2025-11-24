export interface TcpPrinterPlugin {
    send(options: {
      ip: string;
      port: number;
      data: string;
    }): Promise<void>;
  }
  