import { createClient } from '@supabase/supabase-js';

export const supabase = createClient(
  'https://fyabmgdfjmyskulfianx.supabase.co',
  'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImZ5YWJtZ2Rmam15c2t1bGZpYW54Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDM1ODU1OTIsImV4cCI6MjA1OTE2MTU5Mn0.KHg2zvEZ8BFhyBdIpn8ERNA0I9uLcj3BTqBAUtX-R8M'
);
