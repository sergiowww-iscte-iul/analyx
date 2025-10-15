CREATE TABLE analyx.dbo.artifact
(
    idClassMetric int IDENTITY(1,1) NOT NULL primary key,
    nmClass       varchar(100) NOT NULL,
    qtLinesOfCode int          NOT NULL,
    tpArtifact    tinyint      NOT NULL,
    qtCoupling    int          NULL
);
EXEC analyx.sys.sp_addextendedproperty 'MS_Description', N'Artifact type: method or class', 'schema', N'dbo', 'table', N'artifact', 'column', N'tpArtifact';
