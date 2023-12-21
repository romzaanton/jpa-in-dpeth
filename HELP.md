### Read more about SQM

[Link to SQM methodology description](https://github.com/hibernate/hibernate-orm/blob/main/design/sqm.adoc)

### Build DB image for test purpose only

```bash
docker build -t postgres-ispell:0.0.1 -f .\deployment\Dockerfile --progress=plain  --no-cache .
```
