export class TcpPrinterWeb {
    async send(): Promise<void> {
      throw new Error('TCP printing not supported on web');
    }
  }
  