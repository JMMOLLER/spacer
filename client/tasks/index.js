import express from 'express';
import { cron } from "./cron.js";

const app = express();
app.use('/cron', cron);