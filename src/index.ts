import { registerPlugin } from '@capacitor/core';
import type { TcpPrinterPlugin } from './definitions.ts';
export const TcpPrinter = registerPlugin<TcpPrinterPlugin>('TcpPrint');
export * from './definitions.js';
