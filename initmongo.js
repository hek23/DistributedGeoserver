use visordb;
db.createUser(
        {
            user: 'visordbUser',
            pwd: 'YnCxRTCafgtr4BJ!',
            roles: [
                {
                    role: "readWrite",
                    db: "visordb"
                }
            ]
        }
);

db.users.ensureIndex( { username: 1 }, { unique: true } );

