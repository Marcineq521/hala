INSERT INTO machines(code, name)
VALUES
  ('D1','Drukarka D1'),
  ('D2','Drukarka D2'),
  ('D3','Drukarka D3')
ON CONFLICT (code) DO NOTHING;