export function StatusBadge({ status }) {
  const isOnline = status?.status === 'UP';
  const label = isOnline ? 'API online' : 'API offline';

  return (
    <span className={`status-badge ${isOnline ? 'status-badge--online' : 'status-badge--offline'}`}>
      <span className="status-badge__dot" aria-hidden="true" />
      {label}
    </span>
  );
}
