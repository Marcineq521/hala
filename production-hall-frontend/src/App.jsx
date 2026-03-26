import { useEffect, useState } from "react";
import "./App.css";

function App() {
  const [token, setToken] = useState(localStorage.getItem("token") || "");
  const [machines, setMachines] = useState([]);
  const [logs, setLogs] = useState([]);
  const [workOrders, setWorkOrders] = useState([]);

  const [selectedMachine, setSelectedMachine] = useState("");
  const [selectedWorkOrder, setSelectedWorkOrder] = useState("");
  const [operatorName, setOperatorName] = useState("");

  const [view, setView] = useState("dashboard");

  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  const authHeaders = token
    ? {
        Authorization: "Bearer " + token,
      }
    : {};

  const handleLogin = async () => {
    try {
      const res = await fetch("/api/auth/login", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ email, password }),
      });

      if (!res.ok) {
        alert("Błąd logowania");
        return;
      }

      const data = await res.json();
      localStorage.setItem("token", data.token);
      setToken(data.token);
    } catch (err) {
      alert("Nie udało się połączyć z backendem");
      console.error(err);
    }
  };

  const handleLogout = () => {
    localStorage.removeItem("token");
    setToken("");
    setMachines([]);
    setLogs([]);
    setWorkOrders([]);
    setSelectedMachine("");
    setSelectedWorkOrder("");
    setOperatorName("");
    setView("dashboard");
  };

  const fetchDashboard = async () => {
  try {
    console.log("TOKEN STATE:", token);
    console.log("TOKEN LS:", localStorage.getItem("token"));

    const res = await fetch("/api/dashboard/machines", {
      headers: {
        Authorization: "Bearer " + token,
      },
    });

    console.log("DASHBOARD STATUS:", res.status);

    const text = await res.text();
    console.log("DASHBOARD RAW:", text);

    if (!res.ok) {
      return;
    }

    const data = JSON.parse(text);
    console.log("DASHBOARD JSON:", data);

    setMachines(Array.isArray(data) ? data : []);
  } catch (err) {
    console.error("DASHBOARD ERROR:", err);
  }
};

  const fetchLogs = async () => {
    try {
      const res = await fetch("/api/event-logs", {
        headers: authHeaders,
      });

      if (!res.ok) return;
      const data = await res.json();
      setLogs(Array.isArray(data) ? data : []);
    } catch (err) {
      console.error("Błąd logów:", err);
    }
  };

  const fetchWorkOrders = async () => {
    try {
      const res = await fetch("/api/work-orders", {
        headers: authHeaders,
      });

      if (!res.ok) return;
      const data = await res.json();
      setWorkOrders(Array.isArray(data) ? data : []);
    } catch (err) {
      console.error("Błąd work orderów:", err);
    }
  };

  useEffect(() => {
    if (!token) return;

    fetchDashboard();
    fetchLogs();
    fetchWorkOrders();

    const interval = setInterval(() => {
      fetchDashboard();
      fetchLogs();
    }, 5000);

    return () => clearInterval(interval);
  }, [token]);

  const startAssignment = async () => {
    if (!selectedMachine || !operatorName.trim()) {
      alert("Wybierz maszynę i wpisz operatora");
      return;
    }

    try {
      const res = await fetch("/api/assignments/start", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          ...authHeaders,
        },
        body: JSON.stringify({
          machineId: selectedMachine,
          operatorName: operatorName,
          workOrderId: selectedWorkOrder || null,
        }),
      });

      if (!res.ok) {
        const text = await res.text();
        alert("Nie udało się rozpocząć assignmentu: " + text);
        return;
      }

      await fetchDashboard();
      await fetchLogs();
      alert("Assignment rozpoczęty");
    } catch (err) {
      console.error(err);
      alert("Błąd połączenia");
    }
  };

  const endAssignment = async () => {
    if (!selectedMachine) {
      alert("Wybierz maszynę");
      return;
    }

    try {
      const res = await fetch("/api/assignments/end", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          ...authHeaders,
        },
        body: JSON.stringify({
          machineId: selectedMachine,
        }),
      });

      if (!res.ok) {
        const text = await res.text();
        alert("Nie udało się zakończyć assignmentu: " + text);
        return;
      }

      await fetchDashboard();
      await fetchLogs();
      alert("Assignment zakończony");
    } catch (err) {
      console.error(err);
      alert("Błąd połączenia");
    }
  };

  const changeWorkOrderStatus = async (status) => {
    if (!selectedWorkOrder) {
      alert("Wybierz zlecenie");
      return;
    }

    try {
      const res = await fetch(`/api/work-orders/${selectedWorkOrder}/status`, {
        method: "PATCH",
        headers: {
          "Content-Type": "application/json",
          ...authHeaders,
        },
        body: JSON.stringify({ status }),
      });

      if (!res.ok) {
        const text = await res.text();
        alert("Nie udało się zmienić statusu: " + text);
        return;
      }

      await fetchDashboard();
      await fetchLogs();
      await fetchWorkOrders();
      alert("Status zmieniony");
    } catch (err) {
      console.error(err);
      alert("Błąd połączenia");
    }
  };

  const getMachineClass = (machine) => {
    if (!machine.occupied) return "machine-card idle";
    if (machine.workOrderStatus === "IN_PROGRESS") return "machine-card working";
    if (machine.workOrderStatus === "PAUSED") return "machine-card paused";
    if (machine.workOrderStatus === "FAILED") return "machine-card failed";
    if (machine.workOrderStatus === "DONE") return "machine-card done";
    return "machine-card";
  };

  const getMachineLabel = (machine) => {
    if (!machine.occupied) return "Wolna";
    if (machine.workOrderStatus === "IN_PROGRESS") return "Pracuje";
    if (machine.workOrderStatus === "PAUSED") return "Pauza";
    if (machine.workOrderStatus === "FAILED") return "Awaria";
    if (machine.workOrderStatus === "DONE") return "Zakończona";
    return machine.workOrderStatus || "Aktywna";
  };

  if (!token) {
    return (
      <div className="login-page">
        <div className="login-card">
          <div className="login-badge">Production Hall</div>
          <h1>Panel logowania</h1>
          <p>Zaloguj się do systemu hali produkcyjnej.</p>

          <div className="form-group">
            <label>Email</label>
            <input
              type="text"
              placeholder="admin@firma.pl"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
            />
          </div>

          <div className="form-group">
            <label>Hasło</label>
            <input
              type="password"
              placeholder="••••••••"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
            />
          </div>

          <button className="primary-btn full-btn" onClick={handleLogin}>
            Zaloguj
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="app-shell">
      <aside className="sidebar">
        <div>
          <div className="logo-box">PH</div>
          <h2>Production Hall</h2>
          <p className="sidebar-subtitle">Panel operatorski / administracyjny</p>
        </div>

        <nav className="sidebar-nav">
          <button
            className={view === "dashboard" ? "nav-btn active" : "nav-btn"}
            onClick={() => setView("dashboard")}
          >
            Dashboard
          </button>
          <button
            className={view === "logs" ? "nav-btn active" : "nav-btn"}
            onClick={() => setView("logs")}
          >
            Logi
          </button>
          <button
            className={view === "actions" ? "nav-btn active" : "nav-btn"}
            onClick={() => setView("actions")}
          >
            Akcje
          </button>
        </nav>

        <button className="logout-btn" onClick={handleLogout}>
          Wyloguj
        </button>
      </aside>

      <main className="main-content">
        <div className="topbar">
          <div>
            <h1>
              {view === "dashboard" && "Dashboard hali"}
              {view === "logs" && "Dziennik zdarzeń"}
              {view === "actions" && "Sterowanie i akcje"}
            </h1>
          
          </div>
        </div>

        {view === "dashboard" && (
          <div className="dashboard-grid">
            {machines.map((m) => (
              <div key={m.machineId} className={getMachineClass(m)}>
                <div className="machine-top">
                  <span className="machine-code">{m.machineCode}</span>
                  <span className="status-pill">{getMachineLabel(m)}</span>
                </div>

                <h3>{m.machineName}</h3>

                <div className="machine-info">
                  <div>
                    <span>Operator</span>
                    <strong>{m.operatorName || "-"}</strong>
                  </div>
                  <div>
                    <span>Zlecenie</span>
                    <strong>{m.workOrderNo || "-"}</strong>
                  </div>
                  <div>
                    <span>Tytuł</span>
                    <strong>{m.workOrderTitle || "-"}</strong>
                  </div>
                  <div>
                    <span>Status</span>
                    <strong>{m.workOrderStatus || "BRAK"}</strong>
                  </div>
                </div>
              </div>
            ))}
          </div>
        )}

        {view === "logs" && (
          <div className="table-card">
            <table className="logs-table">
              <thead>
                <tr>
                  <th>Czas</th>
                  <th>Typ</th>
                  <th>Maszyna</th>
                  <th>Wiadomość</th>
                </tr>
              </thead>
              <tbody>
                {logs.map((log) => (
                  <tr key={log.id}>
                    <td>{log.ts || log.createdAt || "-"}</td>
                    <td>{log.type || "-"}</td>
                    <td>{log.machineId || "-"}</td>
                    <td>{log.message || "-"}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}

        {view === "actions" && (
          <div className="actions-layout">
            <div className="action-card">
              <h3>Wybór danych</h3>

              <div className="form-group">
                <label>Maszyna</label>
                <select
                  value={selectedMachine}
                  onChange={(e) => setSelectedMachine(e.target.value)}
                >
                  <option value="">-- wybierz maszynę --</option>
                  {machines.map((m) => (
                    <option key={m.machineId} value={m.machineId}>
                      {m.machineCode} - {m.machineName}
                    </option>
                  ))}
                </select>
              </div>

              <div className="form-group">
                <label>Operator</label>
                <input
                  type="text"
                  placeholder="np. Marcin"
                  value={operatorName}
                  onChange={(e) => setOperatorName(e.target.value)}
                />
              </div>

              <div className="form-group">
                <label>Zlecenie</label>
                <select
                  value={selectedWorkOrder}
                  onChange={(e) => setSelectedWorkOrder(e.target.value)}
                >
                  <option value="">-- wybierz zlecenie --</option>
                  {workOrders.map((w) => (
                    <option key={w.id} value={w.id}>
                      {w.orderNo} - {w.title} ({w.status})
                    </option>
                  ))}
                </select>
              </div>
            </div>

            <div className="action-card">
              <h3>Assignment</h3>
              <div className="button-grid">
                <button className="primary-btn" onClick={startAssignment}>
                  Start pracy
                </button>
                <button className="secondary-btn" onClick={endAssignment}>
                  Koniec pracy
                </button>
              </div>
            </div>

            <div className="action-card">
              <h3>Status zlecenia</h3>
              <div className="button-grid">
                <button
                  className="primary-btn"
                  onClick={() => changeWorkOrderStatus("IN_PROGRESS")}
                >
                  Start zlecenia
                </button>
                <button
                  className="warning-btn"
                  onClick={() => changeWorkOrderStatus("PAUSED")}
                >
                  Pauza
                </button>
                <button
                  className="success-btn"
                  onClick={() => changeWorkOrderStatus("DONE")}
                >
                  Zakończone
                </button>
                <button
                  className="danger-btn"
                  onClick={() => changeWorkOrderStatus("FAILED")}
                >
                  Awaria
                </button>
              </div>
            </div>
          </div>
        )}
      </main>
    </div>
  );
}

export default App;