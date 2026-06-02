import { useState } from 'react'

const initialData = {
  flights: [
    { id: 'AI101', origin: 'New York (JFK)', destination: 'Los Angeles (LAX)', departure: '2026-05-31 08:00', aircraft: 'Boeing 737', seats: 180, available: 175, price: 299 },
    { id: 'AI202', origin: 'New York (JFK)', destination: 'London (LHR)', departure: '2026-06-01 14:00', aircraft: 'Boeing 777', seats: 250, available: 240, price: 599 },
    { id: 'AI303', origin: 'Los Angeles (LAX)', destination: 'Tokyo (NRT)', departure: '2026-06-02 10:00', aircraft: 'Airbus A380', seats: 300, available: 290, price: 899 },
    { id: 'AI404', origin: 'London (LHR)', destination: 'Dubai (DXB)', departure: '2026-05-31 18:00', aircraft: 'Boeing 787', seats: 220, available: 210, price: 449 },
    { id: 'AI505', origin: 'Paris (CDG)', destination: 'Singapore (SIN)', departure: '2026-06-03 20:00', aircraft: 'Airbus A350', seats: 280, available: 275, price: 749 }
  ],
  passengers: [
    { id: 1, name: 'John Doe', email: 'john.doe@email.com', phone: '555-0100', passport: 'AB123456', nationality: 'American' },
    { id: 2, name: 'Sarah Smith', email: 'sarah.smith@email.com', phone: '555-0200', passport: 'CD789012', nationality: 'British' },
    { id: 3, name: 'Ahmed Hassan', email: 'ahmed.hassan@email.com', phone: '555-0300', passport: 'EF345678', nationality: 'Egyptian' },
    { id: 4, name: 'Wei Chen', email: 'wei.chen@email.com', phone: '555-0400', passport: 'GH901234', nationality: 'Chinese' }
  ],
  bookings: []
}

function generateRef() {
  const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789'
  let ref = ''
  for (let i = 0; i < 6; i++) ref += chars[Math.floor(Math.random() * chars.length)]
  return ref
}

function generateSeats(flightId, bookings) {
  const bookedSeats = bookings.filter(b => b.flightId === flightId).map(b => b.seat)
  const seats = []
  for (let row = 1; row <= 30; row++) {
    for (let col of ['A', 'B', 'C', 'D', 'E', 'F']) {
      const seatId = row + col
      seats.push({
        id: seatId,
        row,
        column: col,
        available: !bookedSeats.includes(seatId),
        class: row <= 3 ? 'First' : row <= 7 ? 'Business' : row <= 12 ? 'Premium' : 'Economy'
      })
    }
  }
  return seats
}

function getStatusBadge(status) {
  const classes = {
    'Confirmed': 'badge-success',
    'Cancelled': 'badge-danger',
    'Checked-in': 'badge-info',
    'Boarded': 'badge-warning'
  }
  return classes[status] || 'badge-info'
}

function PlaneIcon({ size = 18 }) {
  return (
    <svg width={size} height={size} viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <path d="M21 16v-2a4 4 0 0 0-4-4H7.5a2.5 2.5 0 0 0 0 5h8.5" />
      <path d="M3 8h14v8H3z" />
      <path d="m10 12 2-2 2 2" />
    </svg>
  )
}

function GridIcon({ size = 18 }) {
  return (
    <svg width={size} height={size} viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
      <rect x="3" y="3" width="7" height="7" /><rect x="14" y="3" width="7" height="7" />
      <rect x="14" y="14" width="7" height="7" /><rect x="3" y="14" width="7" height="7" />
    </svg>
  )
}

function UsersIcon({ size = 18 }) {
  return (
    <svg width={size} height={size} viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
      <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2" />
      <circle cx="9" cy="7" r="4" />
      <path d="M23 21v-2a4 4 0 0 0-3-3.87" />
      <path d="M16 3.13a4 4 0 0 1 0 7.75" />
    </svg>
  )
}

function TicketIcon({ size = 18 }) {
  return (
    <svg width={size} height={size} viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
      <path d="M2 9a3 3 0 0 1 0 6v2a2 2 0 0 0 2 2h16a2 2 0 0 0 2-2v-2a3 3 0 0 1 0-6V7a2 2 0 0 0-2-2H4a2 2 0 0 0-2 2Z" />
      <path d="M13 5v2" /><path d="M13 17v2" /><path d="M13 11v2" />
    </svg>
  )
}

function CheckIcon({ size = 18 }) {
  return (
    <svg width={size} height={size} viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
      <polyline points="9 11 12 14 22 4" />
      <path d="M21 12v7a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11" />
    </svg>
  )
}

function Login({ onLogin }) {
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState(false)

  function handleSubmit(e) {
    e.preventDefault()
    if (username === 'admin' && password === '123') {
      onLogin()
    } else {
      setError(true)
    }
  }

  return (
    <div className="login-page">
      <div className="login-card">
        <h1>SkyBook</h1>
        <p className="subtitle">Airline Reservation System</p>
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label>Username</label>
            <input
              type="text"
              value={username}
              onChange={e => setUsername(e.target.value)}
              placeholder="Enter username"
              required
            />
          </div>
          <div className="form-group">
            <label>Password</label>
            <input
              type="password"
              value={password}
              onChange={e => setPassword(e.target.value)}
              placeholder="Enter password"
              required
            />
          </div>
          <button type="submit" className="login-btn">Sign In</button>
          <p className={`login-error ${error ? 'show' : ''}`}>Invalid username or password</p>
        </form>
      </div>
    </div>
  )
}

function Sidebar({ activeSection, onNavigate }) {
  const navItems = [
    { id: 'dashboard', label: 'Dashboard', icon: <GridIcon /> },
    { id: 'flights', label: 'Flights', icon: <PlaneIcon /> },
    { id: 'passengers', label: 'Passengers', icon: <UsersIcon /> },
    { id: 'bookings', label: 'Bookings', icon: <TicketIcon /> },
    { id: 'checkin', label: 'Check-in', icon: <CheckIcon /> }
  ]

  return (
    <aside className="sidebar">
      <div className="sidebar-logo">
        <PlaneIcon />
        SkyBook
      </div>
      <nav className="sidebar-nav">
        {navItems.map(item => (
          <button
            key={item.id}
            className={`nav-item ${activeSection === item.id ? 'active' : ''}`}
            onClick={() => onNavigate(item.id)}
          >
            {item.icon}
            {item.label}
          </button>
        ))}
      </nav>
    </aside>
  )
}

function Dashboard({ data }) {
  const totalSeats = data.flights.reduce((sum, f) => sum + f.available, 0)

  return (
    <>
      <div className="stats-grid">
        <div className="stat-card">
          <h4>Total Flights</h4>
          <div className="value">{data.flights.length}</div>
        </div>
        <div className="stat-card">
          <h4>Passengers</h4>
          <div className="value">{data.passengers.length}</div>
        </div>
        <div className="stat-card">
          <h4>Active Bookings</h4>
          <div className="value">{data.bookings.filter(b => b.status !== 'Cancelled').length}</div>
        </div>
        <div className="stat-card">
          <h4>Available Seats</h4>
          <div className="value">{totalSeats}</div>
        </div>
      </div>

      <div className="card">
        <div className="card-header">
          <PlaneIcon />
          Recent Flights
        </div>
        <table className="data-table">
          <thead>
            <tr>
              <th>Flight</th>
              <th>Route</th>
              <th>Departure</th>
              <th>Price</th>
              <th>Status</th>
            </tr>
          </thead>
          <tbody>
            {data.flights.slice(0, 5).map(f => (
              <tr key={f.id}>
                <td><strong>{f.id}</strong></td>
                <td>{f.origin} → {f.destination}</td>
                <td>{f.departure}</td>
                <td>${f.price}</td>
                <td><span className="badge badge-success">Available</span></td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </>
  )
}

function Flights({ flights }) {
  return (
    <div className="card">
      <div className="card-header">
        <PlaneIcon />
        Flight Management
      </div>
      <table className="data-table">
        <thead>
          <tr>
            <th>Flight No.</th>
            <th>Origin</th>
            <th>Destination</th>
            <th>Departure</th>
            <th>Aircraft</th>
            <th>Seats</th>
            <th>Price</th>
          </tr>
        </thead>
        <tbody>
          {flights.map(f => (
            <tr key={f.id}>
              <td><strong>{f.id}</strong></td>
              <td>{f.origin}</td>
              <td>{f.destination}</td>
              <td>{f.departure}</td>
              <td>{f.aircraft}</td>
              <td>{f.available}/{f.seats}</td>
              <td><strong>${f.price}</strong></td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}

function Passengers({ passengers }) {
  return (
    <div className="card">
      <div className="card-header">
        <UsersIcon />
        Passenger Management
      </div>
      <table className="data-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Name</th>
            <th>Email</th>
            <th>Phone</th>
            <th>Passport</th>
            <th>Nationality</th>
          </tr>
        </thead>
        <tbody>
          {passengers.map(p => (
            <tr key={p.id}>
              <td>P-{p.id}</td>
              <td>{p.name}</td>
              <td>{p.email}</td>
              <td>{p.phone}</td>
              <td>{p.passport}</td>
              <td>{p.nationality}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}

function Bookings({ bookings, onCancel }) {
  return (
    <div className="card">
      <div className="card-header">
        <TicketIcon />
        Booking Management
      </div>
      {bookings.length === 0 ? (
        <p className="empty-state">No bookings yet. Create one from the Check-in section.</p>
      ) : (
        <table className="data-table">
          <thead>
            <tr>
              <th>Ref</th>
              <th>Flight</th>
              <th>Passenger</th>
              <th>Seat</th>
              <th>Price</th>
              <th>Status</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {bookings.map(b => (
              <tr key={b.ref}>
                <td><strong>{b.ref}</strong></td>
                <td>{b.flightId}</td>
                <td>{b.passengerName}</td>
                <td>{b.seat}</td>
                <td>${b.price}</td>
                <td><span className={`badge ${getStatusBadge(b.status)}`}>{b.status}</span></td>
                <td>
                  {b.status === 'Confirmed' && (
                    <button className="btn btn-danger btn-sm" onClick={() => onCancel(b.ref)}>Cancel</button>
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  )
}

function Checkin({ bookings, onBook }) {
  const activeBookings = bookings.filter(b => b.status !== 'Cancelled')

  function handleCheckin(ref) {
    const booking = bookings.find(b => b.ref === ref)
    if (booking) booking.status = 'Checked-in'
    onBook([...bookings])
  }

  function handleBoard(ref) {
    const booking = bookings.find(b => b.ref === ref)
    if (booking) booking.status = 'Boarded'
    onBook([...bookings])
  }

  return (
    <div className="card">
      <div className="card-header">
        <CheckIcon />
        Check-in & Boarding
      </div>
      {activeBookings.length === 0 ? (
        <p className="empty-state">No bookings to check-in. Make a booking first.</p>
      ) : (
        <table className="data-table">
          <thead>
            <tr>
              <th>Ref</th>
              <th>Flight</th>
              <th>Passenger</th>
              <th>Seat</th>
              <th>Status</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {activeBookings.map(b => (
              <tr key={b.ref}>
                <td><strong>{b.ref}</strong></td>
                <td>{b.flightId}</td>
                <td>{b.passengerName}</td>
                <td>{b.seat}</td>
                <td><span className={`badge ${getStatusBadge(b.status)}`}>{b.status}</span></td>
                <td>
                  {b.status === 'Confirmed' && (
                    <button className="btn btn-primary btn-sm" onClick={() => handleCheckin(b.ref)}>Check In</button>
                  )}
                  {b.status === 'Checked-in' && (
                    <button className="btn btn-success btn-sm" onClick={() => handleBoard(b.ref)}>Board</button>
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  )
}

function BookingModal({ flights, passengers, onClose, onConfirm }) {
  const [selectedFlight, setSelectedFlight] = useState(flights[0]?.id || '')
  const [selectedPassenger, setSelectedPassenger] = useState(passengers[0]?.id || '')
  const [selectedSeat, setSelectedSeat] = useState(null)
  const [showTicket, setShowTicket] = useState(false)
  const [ticketData, setTicketData] = useState(null)

  const seats = generateSeats(selectedFlight, [])

  function handleConfirm() {
    if (!selectedSeat) {
      alert('Please select a seat first')
      return
    }

    const flight = flights.find(f => f.id === selectedFlight)
    const passenger = passengers.find(p => p.id === parseInt(selectedPassenger))
    const seat = seats.find(s => s.id === selectedSeat)

    if (!seat.available) {
      alert('Seat is already booked')
      return
    }

    const price = flight.price + (seat.row <= 5 ? 50 : seat.row <= 25 ? 25 : 0)

    const booking = {
      ref: generateRef(),
      flightId: selectedFlight,
      passengerId: parseInt(selectedPassenger),
      passengerName: passenger.name,
      seat: selectedSeat,
      price,
      status: 'Confirmed'
    }

    setTicketData({ booking, flight, passenger, seat })
    setShowTicket(true)
  }

  function handleTicketClose() {
    onConfirm({
      ref: ticketData.booking.ref,
      flightId: ticketData.booking.flightId,
      passengerId: ticketData.booking.passengerId,
      passengerName: ticketData.booking.passengerName,
      seat: ticketData.booking.seat,
      price: ticketData.booking.price,
      status: 'Confirmed'
    })
    onClose()
  }

  if (showTicket && ticketData) {
    return (
      <div className="modal-overlay" onClick={handleTicketClose}>
        <div className="modal ticket-modal" onClick={e => e.stopPropagation()}>
          <button className="close-btn" onClick={handleTicketClose}>&times;</button>
          <div className="ticket">
            <h2>BOARDING PASS</h2>
            <div className="ticket-row">
              <span>Reference</span>
              <span>{ticketData.booking.ref}</span>
            </div>
            <div className="ticket-row">
              <span>Flight</span>
              <span>{ticketData.flight.id}</span>
            </div>
            <div className="ticket-row">
              <span>Route</span>
              <span>{ticketData.flight.origin} → {ticketData.flight.destination}</span>
            </div>
            <div className="ticket-row">
              <span>Departure</span>
              <span>{ticketData.flight.departure}</span>
            </div>
            <div className="ticket-row">
              <span>Passenger</span>
              <span>{ticketData.passenger.name}</span>
            </div>
            <div className="ticket-row">
              <span>Seat</span>
              <span className="seat-display">{ticketData.booking.seat}</span>
            </div>
            <div className="ticket-row">
              <span>Class</span>
              <span>{ticketData.seat.class}</span>
            </div>
            <div className="ticket-row">
              <span>Total Paid</span>
              <span>${ticketData.booking.price}</span>
            </div>
          </div>
        </div>
      </div>
    )
  }

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal" onClick={e => e.stopPropagation()}>
        <div className="modal-header">
          <h2>Create New Booking</h2>
          <button className="close-btn" onClick={onClose}>&times;</button>
        </div>

        <div className="form-grid">
          <div className="form-group">
            <label>Select Flight</label>
            <select value={selectedFlight} onChange={e => setSelectedFlight(e.target.value)}>
              {flights.map(f => (
                <option key={f.id} value={f.id}>
                  {f.id} - {f.origin} → {f.destination} (${f.price})
                </option>
              ))}
            </select>
          </div>
          <div className="form-group">
            <label>Select Passenger</label>
            <select value={selectedPassenger} onChange={e => setSelectedPassenger(e.target.value)}>
              {passengers.map(p => (
                <option key={p.id} value={p.id}>
                  {p.name} ({p.passport})
                </option>
              ))}
            </select>
          </div>
        </div>

        <p className="section-title">Select Your Seat</p>
        <div className="seat-grid">
          {seats.map(s => (
            <div
              key={s.id}
              className={`seat ${s.available ? 'available' : 'occupied'} ${selectedSeat === s.id ? 'selected' : ''}`}
              onClick={() => s.available && setSelectedSeat(s.id)}
              title={`Row ${s.row}, Seat ${s.column}, ${s.class}`}
            >
              {s.id}
            </div>
          ))}
        </div>

        {selectedSeat && (
          <p className="selected-seat-info">Selected: {selectedSeat}</p>
        )}

        <button className="btn btn-success confirm-btn" onClick={handleConfirm}>
          Confirm Booking
        </button>
      </div>
    </div>
  )
}

export default function App() {
  const [isLoggedIn, setIsLoggedIn] = useState(false)
  const [activeSection, setActiveSection] = useState('dashboard')
  const [showBookingModal, setShowBookingModal] = useState(false)
  const [data, setData] = useState(initialData)

  function handleLogin() {
    setIsLoggedIn(true)
  }

  function handleLogout() {
    setIsLoggedIn(false)
    setActiveSection('dashboard')
  }

  function handleNavigate(section) {
    setActiveSection(section)
    if (section === 'bookings') {
      setShowBookingModal(true)
    }
  }

  function handleBookingComplete(booking) {
    setData(prev => ({
      ...prev,
      bookings: [...prev.bookings, booking]
    }))
    setShowBookingModal(false)
  }

  function handleCancelBooking(ref) {
    setData(prev => ({
      ...prev,
      bookings: prev.bookings.map(b =>
        b.ref === ref ? { ...b, status: 'Cancelled' } : b
      )
    }))
  }

  if (!isLoggedIn) {
    return (
      <div className="app">
        <Login onLogin={handleLogin} />
      </div>
    )
  }

  return (
    <div className="app">
      <div className="layout">
        <Sidebar activeSection={activeSection} onNavigate={handleNavigate} />

        <main className="main-content">
          <div className="page-header">
            <h1>{activeSection.charAt(0).toUpperCase() + activeSection.slice(1)}</h1>
            <button className="logout-btn" onClick={handleLogout}>Logout</button>
          </div>

          {activeSection === 'dashboard' && <Dashboard data={data} />}
          {activeSection === 'flights' && <Flights flights={data.flights} />}
          {activeSection === 'passengers' && <Passengers passengers={data.passengers} />}
          {activeSection === 'bookings' && (
            <Bookings bookings={data.bookings} onCancel={handleCancelBooking} />
          )}
          {activeSection === 'checkin' && (
            <Checkin
              bookings={data.bookings}
              onBook={bookings => setData(prev => ({ ...prev, bookings }))}
            />
          )}
        </main>
      </div>

      {showBookingModal && (
        <BookingModal
          flights={data.flights}
          passengers={data.passengers}
          onClose={() => setShowBookingModal(false)}
          onConfirm={handleBookingComplete}
        />
      )}
    </div>
  )
}
