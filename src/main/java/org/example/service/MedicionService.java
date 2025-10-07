@Service
public class MedicionService {
  private final MedicionRepository repo;

  public MedicionService(MedicionRepository repo) { this.repo = repo; }

  public Medicion save(Medicion m) {
    if (m.getTimestamp()==null) m.setTimestamp(Instant.now());
    return repo.save(m);
  }

  public List<Medicion> getBySensor(String sensorId, Instant from, Instant to) {
    return repo.findBySensorIdAndTimestampBetween(sensorId, from, to);
  }
}
